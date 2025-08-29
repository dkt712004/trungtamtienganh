package com.dkt.academicservice.repository;

import com.dkt.academicservice.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}