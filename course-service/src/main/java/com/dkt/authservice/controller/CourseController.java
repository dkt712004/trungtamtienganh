package com.dkt.authservice.controller;

import com.dkt.authservice.dto.CourseDto;
import com.dkt.authservice.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * API để xem danh sách tất cả khóa học.
     * Bất kỳ ai có token hợp lệ đều có thể truy cập.
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * API để xem chi tiết một khóa học.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /**
     * API để tạo một khóa học mới.
     * Yêu cầu vai trò QUAN_LY.
     */
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