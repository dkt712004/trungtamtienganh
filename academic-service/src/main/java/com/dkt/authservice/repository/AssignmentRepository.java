package com.dkt.authservice.repository;

import com.dkt.authservice.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Tìm tất cả bài tập của một lớp học
    List<Assignment> findByClassroomId(Long classroomId);
}