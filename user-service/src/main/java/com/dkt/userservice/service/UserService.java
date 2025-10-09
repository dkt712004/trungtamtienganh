package com.dkt.userservice.service;

import com.dkt.userservice.dto.*;
import com.dkt.userservice.entity.User;
import java.util.List;

public interface UserService {

    User registerUser(RegisterRequest request);

    User findUserByEmail(String email);

    User updateUserProfile(String email, UpdateProfileRequest request);

    void updateUserPassword(String email, ChangePasswordRequest request);

    void deactivateUserAccount(String email);

    List<UserDto> findAllUsers();

    User findById(Long id);

    UserDto convertToDto(User user);

    StudentProfileDto findStudentProfileByUserId(Long userId);
}