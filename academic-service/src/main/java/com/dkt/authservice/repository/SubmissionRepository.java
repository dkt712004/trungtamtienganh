package com.dkt.authservice.repository;

import com.dkt.authservice.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // Tìm bài nộp của một học viên cho một bài tập cụ thể
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    // Tìm tất cả bài nộp của một bài tập (dành cho giáo viên)
    List<Submission> findByAssignmentId(Long assignmentId);
}