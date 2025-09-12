package com.dkt.userservice.controller;

import com.dkt.userservice.dto.*;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API công khai để người dùng mới đăng ký.
     * Endpoint này được Gateway cho phép đi qua mà không cần token.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.registerUser(request);
            return ResponseEntity.ok(userService.convertToDto(newUser));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API để lấy thông tin cá nhân của người dùng đang đăng nhập.
     * Nó nhận email của người dùng từ header 'X-User-Email' do Gateway thêm vào.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(@RequestHeader("X-User-Email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * API để cập nhật thông tin cá nhân.
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(@RequestHeader("X-User-Email") String userEmail,
                                                   @RequestBody UpdateProfileRequest request) {
        User updatedUser = userService.updateUserProfile(userEmail, request);
        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    /**
     * API để thay đổi mật khẩu.
     */
    @PutMapping("/change-password")
    public ResponseEntity<String> changeMyPassword(@RequestHeader("X-User-Email") String userEmail,
                                                   @RequestBody ChangePasswordRequest request) {
        try {
            userService.updateUserPassword(userEmail, request);
            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API để xóa (vô hiệu hóa) tài khoản.
     */
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@RequestHeader("X-User-Email") String userEmail) {
        userService.deactivateUserAccount(userEmail);
        return ResponseEntity.ok("Tài khoản của bạn đã được vô hiệu hóa.");
    }

    /**
     * API để lấy danh sách tất cả người dùng.
     * Kiểm tra vai trò thủ công từ header do Gateway cung cấp.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("X-User-Roles") String roles) {
        // Kiểm tra xem chuỗi vai trò có chứa "ROLE_QUAN_LY" không
        if (roles == null || !roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập.");
        }

        List<UserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * API để lấy thông tin chi tiết của một người dùng bất kỳ qua ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("X-User-Roles") String roles) {
        if (roles == null || !roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập.");
        }

        User user = userService.findById(id);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * API nội bộ để các service khác có thể lấy thông tin hồ sơ học viên
     * dựa trên user ID.
     * Endpoint: GET /api/users/{userId}/student-profile
     */
    @GetMapping("/{userId}/student-profile")
    public ResponseEntity<StudentProfileDto> getStudentProfileByUserId(@PathVariable Long userId) {
        try {
            StudentProfileDto profile = userService.findStudentProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}