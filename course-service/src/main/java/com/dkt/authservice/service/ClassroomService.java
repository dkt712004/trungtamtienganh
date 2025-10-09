package com.dkt.authservice.service;

import com.dkt.authservice.dto.ClassroomDto;
import com.dkt.authservice.dto.EnrollmentRequest;
import java.util.List;

public interface ClassroomService {

    List<ClassroomDto> getAllClassrooms();

    ClassroomDto getClassroomById(Long id);

    ClassroomDto createClassroom(ClassroomDto classroomDto);

    ClassroomDto updateClassroom(Long id, ClassroomDto classroomDto);

    void deleteClassroom(Long id);

    void enrollStudents(EnrollmentRequest request);
}