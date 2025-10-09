package com.dkt.authservice.controller;

import com.dkt.authservice.dto.ClassroomDto;
import com.dkt.authservice.dto.EnrollmentRequest;
import com.dkt.authservice.service.ClassroomService;
import com.dkt.authservice.service.ClassroomServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classroom API", description = "Các API để quản lý Lớp học và Ghi danh")
@SecurityRequirement(name = "bearerAuth")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    /**
     * API để xem danh sách tất cả lớp học.
     * Bất kỳ ai có token hợp lệ đều có thể truy cập.
     */
    @Operation(summary = "Lấy danh sách tất cả lớp học")
    @GetMapping
    public ResponseEntity<List<ClassroomDto>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @Operation(summary = "Lấy một lớp học theo id")
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDto> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }

    /**
     * API để tạo một lớp học mới.
     * Yêu cầu vai trò QUAN_LY.
     */
    @Operation(summary = "Tạo một lớp học mới", description = "Chỉ người có vai trò QUAN_LY mới có thể thực hiện.")
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
    @Operation(summary = "Ghi danh học viên vào một lớp học", description = "Chỉ người có vai trò QUAN_LY mới có thể thực hiện.")
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