package com.dkt.authservice.dto;

import lombok.Data;

@Data
public class SubmissionRequest {
    private Long assignmentId; // ID của bài tập mà học viên đang nộp
    private String submissionContent; // Nội dung trả lời của học viên (dạng text)
    private String filePath; // Đường dẫn đến file đính kèm (nếu có)
}