package com.dkt.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "submission_content", columnDefinition = "TEXT")
    private String submissionContent;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "grade")
    private Float grade;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}