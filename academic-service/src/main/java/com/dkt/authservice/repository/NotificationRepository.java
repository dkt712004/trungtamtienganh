package com.dkt.authservice.repository;

import com.dkt.authservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Tìm tất cả thông báo của một lớp học
    List<Notification> findByClassroomId(Long classroomId);
}