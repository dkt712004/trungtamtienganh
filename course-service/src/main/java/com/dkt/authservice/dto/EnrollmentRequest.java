package com.dkt.authservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class EnrollmentRequest {
    private Long classroomId;
    private List<Long> studentIds;
}