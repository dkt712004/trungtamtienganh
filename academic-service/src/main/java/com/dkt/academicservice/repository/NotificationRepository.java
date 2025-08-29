package com.dkt.academicservice.repository;

import com.dkt.academicservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Tìm tất cả thông báo của một lớp học
    List<Notification> findByClassroomId(Long classroomId);
}