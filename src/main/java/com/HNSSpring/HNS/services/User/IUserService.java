package com.HNSSpring.HNS.services.User;

import com.HNSSpring.HNS.dtos.UpdateUserDTO;
import com.HNSSpring.HNS.dtos.UserDTO;
import com.HNSSpring.HNS.dtos.UserLoginDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.User;
import com.HNSSpring.HNS.responses.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
    Number getRoleByUser(String phoneNumber);
    void deleteUser(Integer id);
    User getUserDetailsFromToken(String token) throws Exception;
    Integer getCurrentUserRole(String token) throws Exception;
    User updateUser(Integer userId, UpdateUserDTO userDTO, Integer roleId) throws Exception;
    Page<UserResponse> getAllUsers(String keyword,
                                   Integer roleId,
                                   Pageable pageable);
    public User getUserById(Integer userId) throws Exception;
}
