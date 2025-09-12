package com.dkt.userservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentProfileDto {
    private Long id; // Đây là student.id
    private Long userId;
    private String studentCode;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
}