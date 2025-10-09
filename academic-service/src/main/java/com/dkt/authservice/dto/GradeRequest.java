package com.dkt.authservice.dto;

import lombok.Data;

@Data
public class GradeRequest {
    private Float grade;
    private String feedback;
}