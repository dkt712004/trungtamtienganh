package com.dkt.academicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "classroom_students")
@Data
public class ClassroomStudent {

    @EmbeddedId
    private ClassroomStudentId id;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
}