package com.dkt.authservice.controller;

import com.dkt.authservice.dto.ClassroomDto;
import com.dkt.authservice.dto.EnrollmentRequest;
import com.dkt.authservice.service.ClassroomService;
import org.springframework.http.HttpStatus;
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

    /**
     * API để xem danh sách tất cả lớp học.
     * Bất kỳ ai có token hợp lệ đều có thể truy cập.
     */
    @GetMapping
    public ResponseEntity<List<ClassroomDto>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDto> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }

    /**
     * API để tạo một lớp học mới.
     * Yêu cầu vai trò QUAN_LY.
     */
    @PostMapping
    public ResponseEntity<?> createClassroom(@RequestBody ClassroomDto classroomDto,
                                             @RequestHeader("X-User-Roles") String roles) {
        if (!roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền tạo lớp học.");
        }
        return ResponseEntity.ok(classroomService.createClassroom(classroomDto));
    }

    /**
     * API để ghi danh học viên.
     * Yêu cầu vai trò QUAN_LY.
     */
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollStudentsToClassroom(@RequestBody EnrollmentRequest request,
                                                            @RequestHeader("X-User-Roles") String roles) {
        if (!roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền ghi danh học viên.");
        }
        try {
            classroomService.enrollStudents(request);
            return ResponseEntity.ok("Ghi danh học viên thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}