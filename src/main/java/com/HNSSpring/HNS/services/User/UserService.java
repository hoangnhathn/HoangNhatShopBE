package com.HNSSpring.HNS.services.User;

import com.HNSSpring.HNS.components.JwtTokenUtils;
import com.HNSSpring.HNS.components.LocalizationUtils;
import com.HNSSpring.HNS.dtos.UpdateUserDTO;
import com.HNSSpring.HNS.dtos.UserDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.exception.InvalidOldPasswordException;
import com.HNSSpring.HNS.models.Role;
import com.HNSSpring.HNS.models.User;
import com.HNSSpring.HNS.repositories.RoleRepository;
import com.HNSSpring.HNS.repositories.UserRepository;
import com.HNSSpring.HNS.responses.UserResponse;
import com.HNSSpring.HNS.services.User.IUserService;
import com.HNSSpring.HNS.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;
    private static Integer ADMIN = 1;
    private static Integer USER = 2;
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {

        String phoneNumber = userDTO.getPhoneNumber();
        //kiểm tra xem số điện thoại đã tồn tại hay chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(()->new DataNotFoundException("Role not found"));
        //convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .active(true)
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);
        //kiểm tra nếu có accountId, ko yêu ầu password
        if (userDTO.getFacebookAccountId()==0&&userDTO.getGoogleAccountId()==0){
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);

        }
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Wrong phone number or password");
        }
        User existingUser = optionalUser.get();
        //check password
        if (existingUser.getFacebookAccountId()==0&&existingUser.getGoogleAccountId()==0){
            if(!passwordEncoder.matches(password, existingUser.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        if (!optionalUser.get().isActive()){
            throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_ACTIVE));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, password, existingUser.getAuthorities());
        //authenticate with java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public Number getRoleByUser(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        User user1 =  user.get();
        return user1.getRole().getId();
    }

    @Override
    public Page<UserResponse> getAllUsers(
            String keyword,
            Integer roleId,
            Pageable pageable) {
        Page<User> usersPage = userRepository.searchUsers(keyword,roleId, pageable);
        return usersPage.map(UserResponse::fromUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }


    @Override
    public User getUserById(Integer userId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }

    @Override
    public void deleteUser(Integer id){
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            userRepository.delete(user);
        }
    }

    @Override
    @Transactional
    public User updateUser(Integer userId, UpdateUserDTO updateUserDTO, Integer roleId) throws Exception {
        User existingUser = userRepository.findById(userId).orElseThrow(()->new DataNotFoundException("User not found"));

        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) && userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DataIntegrityViolationException("Phone number already exists");
        }


        if(newPhoneNumber!=null && newPhoneNumber!=""){
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updateUserDTO.getPassword() != null && updateUserDTO.getPassword() != "" &&
                !passwordEncoder.matches(updateUserDTO.getPassword(), existingUser.getPassword())) {
            // Nếu người dùng là client, yêu cầu nhập mật khẩu cũ
            if (roleId==USER) {
                if (updateUserDTO.getOldPassword() == null || !passwordEncoder.matches(updateUserDTO.getOldPassword(), existingUser.getPassword())) {
                    throw new InvalidOldPasswordException("Ole password not true");
                }
            }
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        if (updateUserDTO.getFullName() !=null && updateUserDTO.getFullName() !=""){
            existingUser.setFullName(updateUserDTO.getFullName());
        }

        if (updateUserDTO.getAddress() !=null && updateUserDTO.getAddress() !=""){
            existingUser.setAddress(updateUserDTO.getAddress());
        }

        if (updateUserDTO.getDateOfBirth() !=null){
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }

        if (updateUserDTO.getFacebookAccountId()>0){
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }

        if (updateUserDTO.getGoogleAccountId()>0){
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }

        if (updateUserDTO.getRoleId() != null) {
            // Giả sử bạn có roleRepository để tìm role theo ID
            Role role = roleRepository.findById(updateUserDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại")); // Xử lý nếu role không được tìm thấy
            existingUser.setRole(role);
        }
        if (roleId==ADMIN) {
            existingUser.setActive(updateUserDTO.isActive());
        }
        return userRepository.save(existingUser);
    }

    @Override
    public Integer getCurrentUserRole(String token) throws Exception{
        if(jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token expired");
        }
        Integer roleId = jwtTokenUtil.getRoleFromToken(token);
        if (roleId!=null) {
            return roleId;
        } else {
            throw new DataNotFoundException("role from token not found");
        }
    }

}
