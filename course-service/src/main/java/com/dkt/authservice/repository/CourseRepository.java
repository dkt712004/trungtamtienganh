package com.dkt.authservice.repository;

import com.dkt.authservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}