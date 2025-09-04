package com.dkt.authservice.controller;

import com.dkt.authservice.client.UserClient;
import com.dkt.authservice.client.UserDto;
import com.dkt.authservice.dto.*;
import com.dkt.authservice.service.AcademicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicController {

    private final AcademicService academicService;
    private final UserClient userClient;

    public AcademicController(AcademicService academicService, UserClient userClient) {
        this.academicService = academicService;
        this.userClient = userClient;
    }

    // =============================================
    // API CHO BÀI TẬP (ASSIGNMENT)
    // =============================================

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ROLE_GIAO_VIEN', 'ROLE_QUAN_LY')")
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentDto dto, Principal principal) {
        try {
            Long teacherId = getUserIdFromPrincipal(principal);
            return ResponseEntity.ok(academicService.createAssignment(dto, teacherId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/classrooms/{classroomId}/assignments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AssignmentDto>> getAssignmentsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(academicService.findAssignmentsByClassroom(classroomId));
    }

    // =============================================
    // API CHO BÀI NỘP (SUBMISSION)
    // =============================================

    @PostMapping("/submissions")
    @PreAuthorize("hasRole('ROLE_HOC_VIEN')")
    public ResponseEntity<?> submitAssignment(Principal principal, @RequestBody SubmissionRequest request) {
        try {
            Long studentId = getUserIdFromPrincipal(principal);
            return ResponseEntity.ok(academicService.submitAssignment(studentId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/submissions/{id}/grade")
    @PreAuthorize("hasAnyRole('ROLE_GIAO_VIEN', 'ROLE_QUAN_LY')")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long id, @RequestBody GradeRequest request, Principal principal) {
        try {
            Long teacherId = getUserIdFromPrincipal(principal);
            return ResponseEntity.ok(academicService.gradeSubmission(id, request, teacherId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/assignments/{assignmentId}/my-submission")
    @PreAuthorize("hasRole('ROLE_HOC_VIEN')")
    public ResponseEntity<SubmissionDto> getMySubmission(@PathVariable Long assignmentId, Principal principal) {
        Long studentId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(academicService.getSubmissionForStudent(assignmentId, studentId));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('ROLE_GIAO_VIEN', 'ROLE_QUAN_LY')")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(academicService.findSubmissionsByAssignment(assignmentId));
    }

    // =============================================
    // API CHO THÔNG BÁO (NOTIFICATION)
    // =============================================

    @PostMapping("/notifications")
    @PreAuthorize("hasAnyRole('ROLE_GIAO_VIEN', 'ROLE_QUAN_LY')")
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto dto, Principal principal) {
        Long senderId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(academicService.createNotification(dto, senderId));
    }

    @GetMapping("/classrooms/{classroomId}/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getNotificationsForClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(academicService.findNotificationsByClassroom(classroomId));
    }

    // =============================================
    // HÀM HELPER
    // =============================================

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalStateException("Không thể xác định người dùng đã được xác thực.");
        }
        String email = principal.getName();
        UserDto user = userClient.getUserByEmail(email);
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng tương ứng với email: " + email);
        }
        return user.getId();
    }
}