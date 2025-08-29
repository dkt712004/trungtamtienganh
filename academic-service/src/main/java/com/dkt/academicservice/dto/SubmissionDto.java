package com.dkt.academicservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionDto {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String submissionContent;
    private String filePath;
    private LocalDateTime submittedAt; // Thời gian đã nộp
    private Float grade; // Điểm số
    private String feedback; // Nhận xét của giáo viên
    private LocalDateTime gradedAt; // Thời gian đã chấm
}