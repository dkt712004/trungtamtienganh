package com.dkt.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data; // Thêm import Lombok

import java.io.Serializable;

@Embeddable
@Data // Thêm @Data để tự tạo getter/setter
public class ClassroomStudentId implements Serializable {

    @Column(name = "classroom_id")
    private Long classroomId;

    @Column(name = "student_id")
    private Long studentId;
}