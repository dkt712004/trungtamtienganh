package com.dkt.authservice.service;

import com.dkt.authservice.dto.*;
import java.util.List;

public interface AcademicService {

    // --- Các phương thức cho Assignment ---
    AssignmentDto createAssignment(AssignmentDto dto, Long teacherId);

    // --- Các phương thức cho Submission ---
    SubmissionDto submitAssignment(Long studentId, SubmissionRequest request);
    SubmissionDto gradeSubmission(Long submissionId, GradeRequest request, Long teacherId);
    List<SubmissionDto> findSubmissionsByAssignment(Long assignmentId);

    // --- Các phương thức cho Notification ---
    NotificationDto createNotification(NotificationDto dto, Long senderId);
}