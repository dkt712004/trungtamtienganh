package com.dkt.academicservice.repository;

import com.dkt.academicservice.entity.ClassroomStudent;
import com.dkt.academicservice.entity.ClassroomStudentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomStudentRepository extends JpaRepository<ClassroomStudent, ClassroomStudentId> {
}