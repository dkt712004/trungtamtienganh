package com.dkt.authservice.controller;

import com.dkt.authservice.client.StudentProfileDto;
import com.dkt.authservice.client.UserClient;
import com.dkt.authservice.client.UserDto;
import com.dkt.authservice.dto.*;
import com.dkt.authservice.service.AcademicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Academic API", description = "Các API quản lý hoạt động học thuật (Bài tập, Bài nộp, Thông báo)")
@SecurityRequirement(name = "bearerAuth")
public class AcademicController {

    private final AcademicService academicService;
    private final UserClient userClient;

    public AcademicController(AcademicService academicService, UserClient userClient) {
        this.academicService = academicService;
        this.userClient = userClient; // Gán giá trị
    }

    // API CHO BÀI TẬP (ASSIGNMENT)

    /**
     * API để tạo bài tập mới.
     * Yêu cầu Header "X-User-Id" chứa ID của giáo viên/quản lý.
     * Việc kiểm tra vai trò (có phải GV/QL không) được Gateway đảm nhiệm.
     */
    @Operation(summary = "Tạo bài tập mới", description = "Chỉ Giáo viên hoặc Quản lý mới có thể tạo bài tập cho một lớp học.")
    @PostMapping("/assignments")
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentDto dto,
                                              @RequestHeader("X-User-Id") Long teacherId) {
        try {
            AssignmentDto createdAssignment = academicService.createAssignment(dto, teacherId);
            return ResponseEntity.ok(createdAssignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API để lấy tất cả bài tập của một lớp học cụ thể.
     */

    // =============================================
    // API CHO BÀI NỘP (SUBMISSION)
    // =============================================


    // API cho Học viên nộp bài
    @Operation(summary = "Học viên nộp bài", description = "Chỉ người có vai trò HOC_VIEN mới có thể thực hiện.")
    @PostMapping("/submissions")
    @PreAuthorize("hasRole('ROLE_HOC_VIEN')")
    public ResponseEntity<?> submitAssignment(Principal principal, @RequestBody SubmissionRequest request) {
        try {
            Long studentId = getStudentIdFromPrincipal(principal);
            SubmissionDto submissionDto = academicService.submitAssignment(studentId, request);
            return ResponseEntity.ok(submissionDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API cho Học viên xem lại bài nộp


    // --- HÀM HELPER ĐƯỢC NÂNG CẤP ---
    private Long getStudentIdFromPrincipal(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalStateException("Không thể xác định người dùng đã được xác thực.");
        }
        String email = principal.getName();
        UserDto user = userClient.getUserByEmail(email);
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng với email: " + email);
        }
        StudentProfileDto studentProfile = userClient.getStudentProfileByUserId(user.getId());
        if (studentProfile == null || studentProfile.getId() == null) {
            throw new RuntimeException("Không tìm thấy hồ sơ học viên cho người dùng: " + email);
        }
        return studentProfile.getId();
    }

    /**
     * API cho giáo viên chấm điểm.
     * Yêu cầu Header "X-User-Id" chứa ID của giáo viên.
     */
    @Operation(summary = "Giáo viên chấm điểm bài nộp", description = "Chỉ Giáo viên hoặc Quản lý của lớp học đó mới có thể chấm điểm.")
    @PutMapping("/submissions/{id}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long id,
                                             @RequestBody GradeRequest request,
                                             @RequestHeader("X-User-Id") Long teacherId) {
        try {
            SubmissionDto submissionDto = academicService.gradeSubmission(id, request, teacherId);
            return ResponseEntity.ok(submissionDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * API cho xem tất cả bài nộp của một bài tập.
     */
    @Operation(summary = "Giáo viên hoặc quản lý xem tất cả bài nộp của một bài tập.", description = "Chỉ Giáo viên hoặc Quản lý của lớp học đó mới có thể xem.")
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(academicService.findSubmissionsByAssignment(assignmentId));
    }

    // =============================================
    // API CHO THÔNG BÁO (NOTIFICATION)
    // =============================================

    /**
     * API để tạo thông báo mới.
     * Yêu cầu Header "X-User-Id" chứa ID của người gửi.
     */
    @Operation(summary = "Tạo thong bao moi", description = "Chỉ Giáo viên hoặc Quản lý co the tao thong bao moi.")
    @PostMapping("/notifications")
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto dto,
                                                              @RequestHeader("X-User-Id") Long senderId) {
        return ResponseEntity.ok(academicService.createNotification(dto, senderId));
    }


}