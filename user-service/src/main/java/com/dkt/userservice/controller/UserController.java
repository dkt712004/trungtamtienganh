package com.dkt.userservice.controller;

import com.dkt.userservice.dto.ChangePasswordRequest;
import com.dkt.userservice.dto.RegisterRequest;
import com.dkt.userservice.dto.UpdateProfileRequest;
import com.dkt.userservice.dto.UserDto;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API công khai để người dùng mới đăng ký.
     * Endpoint: POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.registerUser(request);
            // Sử dụng hàm convertToDto để trả về dữ liệu an toàn và đầy đủ vai trò
            UserDto userDto = userService.convertToDto(newUser);
            return ResponseEntity.ok(userDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API được bảo vệ, trả về thông tin cá nhân của người dùng đang đăng nhập.
     * Endpoint: GET /api/users/me
     */
    @GetMapping("/me/{email}")
    public ResponseEntity<UserDto> getMyProfile(@PathVariable String email) {
        // `principal.getName()` sẽ chứa email của user
        User user = userService.findUserByEmail(email);
        UserDto userDto = userService.convertToDto(user);
        return ResponseEntity.ok(userDto);
    }

    /**
     * API được bảo vệ, cho phép người dùng cập nhật thông tin cá nhân.
     * Endpoint: PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(Principal principal, @RequestBody UpdateProfileRequest request) {
        User updatedUser = userService.updateUserProfile(principal.getName(), request);
        UserDto userDto = userService.convertToDto(updatedUser);
        return ResponseEntity.ok(userDto);
    }

    // API được cho phép người dùng thay đổi mật khẩu.

    @PutMapping("/change-password")
    public ResponseEntity<String> changeMyPassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        try {
            userService.updateUserPassword(principal.getName(), request);
            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API được cho phép người dùng xóa tài khoản của mình.

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(Principal principal) {
        userService.deactivateUserAccount(principal.getName());
        return ResponseEntity.ok("Tài khoản của bạn đã được vô hiệu hóa.");
    }
}