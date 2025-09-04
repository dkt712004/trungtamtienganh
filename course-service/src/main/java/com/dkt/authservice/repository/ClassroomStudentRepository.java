package com.dkt.authservice.repository;

import com.dkt.authservice.entity.ClassroomStudent;
import com.dkt.authservice.entity.ClassroomStudentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomStudentRepository extends JpaRepository<ClassroomStudent, ClassroomStudentId> {
}