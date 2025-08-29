package com.dkt.academicservice.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
public class ClassroomDto {
    private Long id;
    private String name;
    private Long courseId;
    private Long teacherId; // <-- Đây là trường chúng ta cần để xác thực
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleInfo;
}