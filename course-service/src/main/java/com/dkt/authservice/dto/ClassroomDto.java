package com.dkt.authservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClassroomDto {
    private Long id;
    private String name;
    private Long courseId;
    private Long teacherId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleInfo;
}