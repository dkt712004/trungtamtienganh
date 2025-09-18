package com.dkt.userservice.controller;

import com.dkt.userservice.dto.*;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.service.UserService;
import com.dkt.userservice.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Các API để quản lý người dùng")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Đăng ký tài khoản người dùng mới", description = "API công khai để người dùng mới đăng ký. Mặc định gán vai trò HOC_VIEN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ (ví dụ: email đã tồn tại)")
    })
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
    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy thông tin chi tiết của người dùng đang đăng nhập.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(@RequestHeader("X-User-Email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * API để cập nhật thông tin cá nhân.
     */
    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Cập nhật thông tin chi tiết của người dùng đang đăng nhập.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(@RequestHeader("X-User-Email") String userEmail,
                                                   @RequestBody UpdateProfileRequest request) {
        User updatedUser = userService.updateUserProfile(userEmail, request);
        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    /**
     * API để thay đổi mật khẩu.
     */
    @Operation(summary = "Thay đổi mật khẩu", description = "Thay đổi mật khẩu của người dùng đang đăng nhập.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu cũ không chính xác")
    })
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
    @Operation(summary = "Xóa (vô hiệu hóa) tài khoản", description = "Vô hiệu hóa tài khoản của người dùng đang đăng nhập.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@RequestHeader("X-User-Email") String userEmail) {
        userService.deactivateUserAccount(userEmail);
        return ResponseEntity.ok("Tài khoản của bạn đã được vô hiệu hóa.");
    }

    /**
     * API để lấy danh sách tất cả người dùng.
     * Kiểm tra vai trò thủ công từ header do Gateway cung cấp.
     */
    @Operation(summary = "Lấy danh sách tất cả người dùng", description = "API chỉ dành cho Quản lý. Yêu cầu vai trò ROLE_QUAN_LY.", security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(summary = "Lấy thông tin chi tiết của một người dùng bất kỳ qua ID.", description = "API chỉ dành cho Quản lý. Yêu cầu vai trò ROLE_QUAN_LY.", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "Lấy hồ sơ học viên (Nội bộ)", description = "API nội bộ để các service khác lấy thông tin hồ sơ học viên dựa trên userId. API này không nên được gọi trực tiếp từ client.", hidden = true)
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