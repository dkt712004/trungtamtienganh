package com.dkt.authservice.controller;

import com.dkt.authservice.dto.ClassroomDto;
import com.dkt.authservice.dto.EnrollmentRequest;
import com.dkt.authservice.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDto>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDto> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }

    @PostMapping
    public ResponseEntity<ClassroomDto> createClassroom(@RequestBody ClassroomDto classroomDto) {
        return ResponseEntity.ok(classroomService.createClassroom(classroomDto));
    }

    /**
     * API để ghi danh một hoặc nhiều học viên vào một lớp học.
     * Endpoint: POST /api/classrooms/enroll
     */
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollStudentsToClassroom(@RequestBody EnrollmentRequest request) {
        try {
            classroomService.enrollStudents(request);
            return ResponseEntity.ok("Ghi danh học viên thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}