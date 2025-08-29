package com.dkt.academicservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/api/classrooms/{id}")
    ClassroomDto getClassroomById(@PathVariable("id") Long id);
}