package com.dkt.academicservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class EnrollmentRequest {
    private Long classroomId;
    private List<Long> studentIds;
}