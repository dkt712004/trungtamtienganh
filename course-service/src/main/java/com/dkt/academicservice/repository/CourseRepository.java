package com.dkt.academicservice.repository;

import com.dkt.academicservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}