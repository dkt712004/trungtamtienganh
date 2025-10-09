package com.dkt.authservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private Long id;
    private Long classroomId; // Lớp học mà bài tập này được giao
    private String title; // Tiêu đề của bài tập
    private String content; // Nội dung, yêu cầu chi tiết
    private LocalDateTime dueDate; // Hạn chót nộp bài
    private LocalDateTime createdAt; // Thời gian tạo (thường được set tự động)
}