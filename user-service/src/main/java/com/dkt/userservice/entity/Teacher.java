package com.dkt.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "teachers")
@Data
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "teacher_code")
    private String teacherCode;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "bio")
    private String bio;
}