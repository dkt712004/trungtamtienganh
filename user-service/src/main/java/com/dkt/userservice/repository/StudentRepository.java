package com.dkt.userservice.repository;

import com.dkt.userservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // --- THÊM PHƯƠNG THỨC NÀY VÀO ---
    /**
     * Tìm kiếm hồ sơ học viên (Student) dựa trên khóa ngoại user_id.
     * Spring Data JPA sẽ tự động tạo query cho phương thức này.
     * @param userId ID của người dùng.
     * @return Một Optional chứa Student nếu tìm thấy.
     */
    Optional<Student> findByUserId(Long userId);
}