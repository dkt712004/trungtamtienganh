package com.dkt.authservice.controller;

import com.dkt.authservice.dto.CourseDto;
import com.dkt.authservice.service.CourseService;
import com.dkt.authservice.service.CourseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course API", description = "Các API để quản lý Khóa học")
@SecurityRequirement(name = "bearerAuth") // Ap dung yeu cau xac thuc cho tat ca cac API trong controller nay
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Lấy danh sách tất cả khóa học")
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }


    @Operation(summary = "Lấy thông tin chi tiết một khóa học qua ID")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /**
     * API để tạo một khóa học mới.
     * Yêu cầu vai trò QUAN_LY.
     */
    @Operation(summary = "Tạo một khóa học mới", description = "Chỉ người có vai trò QUAN_LY mới có thể thực hiện.")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDto courseDto,
                                          @RequestHeader("X-User-Roles") String roles) {
        // Kiểm tra vai trò thủ công
        if (!roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền tạo khóa học.");
        }
        CourseDto newCourse = courseService.createCourse(courseDto);
        return ResponseEntity.ok(newCourse);
    }

    /**
     * API để cập nhật một khóa học.
     * Yêu cầu vai trò QUAN_LY.
     */
    @Operation(summary = "Cập nhật thông tin một khóa học", description = "Chỉ người có vai trò QUAN_LY mới có thể thực hiện.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id,
                                          @RequestBody CourseDto courseDto,
                                          @RequestHeader("X-User-Roles") String roles) {
        if (!roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền cập nhật khóa học.");
        }
        CourseDto updatedCourse = courseService.updateCourse(id, courseDto);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * API để xóa một khóa học.
     * Yêu cầu vai trò QUAN_LY.
     */
    @Operation(summary = "Xóa một khóa học", description = "Chỉ người có vai trò QUAN_LY mới có thể thực hiện.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id,
                                               @RequestHeader("X-User-Roles") String roles) {
        if (!roles.contains("ROLE_QUAN_LY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền xóa khóa học.");
        }
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Khóa học với ID " + id + " đã được xóa.");
    }
}