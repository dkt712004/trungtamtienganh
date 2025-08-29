package com.dkt.academicservice.service;

import com.dkt.academicservice.client.ClassroomDto;
import com.dkt.academicservice.client.CourseClient;
import com.dkt.academicservice.dto.*;
import com.dkt.academicservice.entity.Assignment;
import com.dkt.academicservice.entity.Notification;
import com.dkt.academicservice.entity.Submission;
import com.dkt.academicservice.repository.AssignmentRepository;
import com.dkt.academicservice.repository.NotificationRepository;
import com.dkt.academicservice.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AcademicService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final NotificationRepository notificationRepository;
    private final CourseClient courseClient;

    public AcademicService(AssignmentRepository assignmentRepo,
                           SubmissionRepository submissionRepo,
                           NotificationRepository notificationRepo,
                           CourseClient courseClient) {
        this.assignmentRepository = assignmentRepo;
        this.submissionRepository = submissionRepo;
        this.notificationRepository = notificationRepo;
        this.courseClient = courseClient;
    }

    // =============================================
    // LOGIC CHO BÀI TẬP (ASSIGNMENT)
    // =============================================

    @Transactional
    public AssignmentDto createAssignment(AssignmentDto dto, Long teacherId) {
        ClassroomDto classroom = courseClient.getClassroomById(dto.getClassroomId());
        if (!Objects.equals(teacherId, classroom.getTeacherId())) {
            throw new IllegalStateException("Bạn không phải là giáo viên của lớp học này để có thể giao bài.");
        }

        Assignment assignment = new Assignment();
        assignment.setClassroomId(dto.getClassroomId());
        assignment.setTitle(dto.getTitle());
        assignment.setContent(dto.getContent());
        assignment.setDueDate(dto.getDueDate());
        assignment.setCreatedAt(LocalDateTime.now());

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return convertToDto(savedAssignment);
    }

    public List<AssignmentDto> findAssignmentsByClassroom(Long classroomId) {
        List<Assignment> assignments = assignmentRepository.findByClassroomId(classroomId);
        return assignments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // =============================================
    // LOGIC CHO BÀI NỘP (SUBMISSION)
    // =============================================

    @Transactional
    public SubmissionDto submitAssignment(Long studentId, SubmissionRequest request) {
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Bài tập không tồn tại."));

        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("Đã hết hạn nộp bài.");
        }

        submissionRepository.findByAssignmentIdAndStudentId(request.getAssignmentId(), studentId)
                .ifPresent(s -> { throw new IllegalStateException("Bạn đã nộp bài tập này rồi."); });

        Submission submission = new Submission();
        submission.setAssignmentId(request.getAssignmentId());
        submission.setStudentId(studentId);
        submission.setSubmissionContent(request.getSubmissionContent());
        submission.setFilePath(request.getFilePath());
        submission.setSubmittedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);
        return convertToDto(savedSubmission);
    }

    @Transactional
    public SubmissionDto gradeSubmission(Long submissionId, GradeRequest request, Long teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp."));

        Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập tương ứng với bài nộp."));

        ClassroomDto classroom = courseClient.getClassroomById(assignment.getClassroomId());
        if (!Objects.equals(teacherId, classroom.getTeacherId())) {
            throw new IllegalStateException("Bạn không phải là giáo viên của lớp học này để có thể chấm bài.");
        }

        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());
        submission.setGradedAt(LocalDateTime.now());

        Submission gradedSubmission = submissionRepository.save(submission);
        return convertToDto(gradedSubmission);
    }

    public SubmissionDto getSubmissionForStudent(Long assignmentId, Long studentId) {
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp của bạn cho bài tập này."));
        return convertToDto(submission);
    }

    public List<SubmissionDto> findSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // =============================================
    // LOGIC CHO THÔNG BÁO (NOTIFICATION)
    // =============================================

    @Transactional
    public NotificationDto createNotification(NotificationDto dto, Long senderId) {
        // Có thể thêm logic kiểm tra xem sender có quyền gửi thông báo cho lớp này không
        Notification notification = new Notification();
        notification.setClassroomId(dto.getClassroomId());
        notification.setSenderId(senderId);
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDto(savedNotification);
    }

    public List<NotificationDto> findNotificationsByClassroom(Long classroomId) {
        return notificationRepository.findByClassroomId(classroomId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // =============================================
    // CÁC HÀM TIỆN ÍCH CHUYỂN ĐỔI DTO
    // =============================================

    private AssignmentDto convertToDto(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(assignment.getId());
        dto.setClassroomId(assignment.getClassroomId());
        dto.setTitle(assignment.getTitle());
        dto.setContent(assignment.getContent());
        dto.setDueDate(assignment.getDueDate());
        dto.setCreatedAt(assignment.getCreatedAt());
        return dto;
    }

    private SubmissionDto convertToDto(Submission submission) {
        SubmissionDto dto = new SubmissionDto();
        dto.setId(submission.getId());
        dto.setAssignmentId(submission.getAssignmentId());
        dto.setStudentId(submission.getStudentId());
        dto.setSubmissionContent(submission.getSubmissionContent());
        dto.setFilePath(submission.getFilePath());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setGrade(submission.getGrade());
        dto.setFeedback(submission.getFeedback());
        dto.setGradedAt(submission.getGradedAt());
        return dto;
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setClassroomId(notification.getClassroomId());
        dto.setSenderId(notification.getSenderId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}