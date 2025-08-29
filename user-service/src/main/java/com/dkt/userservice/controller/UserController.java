package com.dkt.userservice.controller;

import com.dkt.userservice.dto.*;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    // API tìm tất cả người dùng, chỉ dành cho người có vai trò QUAN_LY
    @GetMapping("/find")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // API này chỉ dành cho người có vai trò QUAN_LY
    @GetMapping("/find/{id}")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * API nội bộ (inter-service) để các service khác có thể lấy thông tin
     * cơ bản của user dựa trên email.
     * Endpoint: GET /api/users/by-email?email=...
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserSimpleDto> getUserByEmail(@RequestParam("email") String email) {
        try {
            UserSimpleDto userDto = userService.findSimpleUserByEmail(email);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API để các service khác lấy thông tin user qua ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserByIdForService(@PathVariable Long id) {
        User user = userService.findById(id); // Giả sử đã có hàm này trong service
        return ResponseEntity.ok(userService.convertToDto(user));
    }

}