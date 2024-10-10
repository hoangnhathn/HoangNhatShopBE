package com.HNSSpring.HNS.Controller;

import com.HNSSpring.HNS.dtos.*;
import com.HNSSpring.HNS.models.User;
import com.HNSSpring.HNS.responses.*;
import com.HNSSpring.HNS.services.User.IUserService;
import com.HNSSpring.HNS.components.LocalizationUtils;
import com.HNSSpring.HNS.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<UserListResponse> getUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "role_id") Integer roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortField, // Thêm trường sắp xếp
            @RequestParam(defaultValue = "asc") String sortDirection // Thêm hướng sắp xếp
    ){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        //tạo pageable từ trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page,limit, sort);
        Page<UserResponse> userPage = userService.getAllUsers(keyword,roleId,pageRequest);
        //lấy tổng số trang
        int totalPages = userPage.getTotalPages();
        List<UserResponse> users = userPage.getContent();

        return ResponseEntity.ok(UserListResponse
                .builder()
                .users(users)
                .totalPages(totalPages)
                .build());

    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result) {
        try{
            if (result.hasErrors()) {
                List<?> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_FAILED,errorMessages))
                                .build());
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                        .build());
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    RegisterResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .user(user)
                    .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    RegisterResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_FAILED,e.getMessage()))
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request){
        //kiểm tra thông tin đăng nhập và sinh token
        // Trả về token trong response
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            Number roleId = userService.getRoleByUser(userLoginDTO.getPhoneNumber());

            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .roleId(roleId)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED,e.getMessage()))
                    .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Valid @PathVariable Integer id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorization
    ){
        try{
            String extractedToken = authorization.substring(7);
            Integer roleId = userService.getCurrentUserRole(extractedToken);
            User user = userService.updateUser(id,updateUserDTO,roleId);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable Integer id){
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", localizationUtils.getLocalizedMessage(MessageKeys.DELETE_USER_SUCCESSFULLY));
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String authorization){
        try {
            String extractedToken = authorization.substring(7); // loại bỏ "Bearer "
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable Integer userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorization
    ){
         try{
             String extractedToken = authorization.substring(7);
             User user = userService.getUserDetailsFromToken(extractedToken);
             Integer roleId = userService.getCurrentUserRole(extractedToken);
             if (user.getId()!=userId){
                 ResponseEntity.status(HttpStatus.FORBIDDEN).build();
             }
             User updatedUser = userService.updateUser(userId, updatedUserDTO, roleId);
             return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
         } catch (Exception e){
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }

}
