package com.dkt.authservice.repository;

import com.dkt.authservice.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}