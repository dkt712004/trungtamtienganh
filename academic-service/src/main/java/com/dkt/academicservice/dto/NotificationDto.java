package com.dkt.academicservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private Long classroomId; // Lớp học sẽ nhận thông báo
    private Long senderId; // ID của người gửi (giáo viên hoặc quản lý)
    private String title; // Tiêu đề thông báo
    private String content; // Nội dung thông báo
    private LocalDateTime createdAt; // Thời gian tạo
}