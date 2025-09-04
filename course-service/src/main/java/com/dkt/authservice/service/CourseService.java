package com.dkt.authservice.service;

import com.dkt.authservice.dto.CourseDto;
import com.dkt.authservice.entity.Course;
import com.dkt.authservice.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
        return convertToDto(course);
    }

    @Transactional
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setLevel(courseDto.getLevel());

        Course savedCourse = courseRepository.save(course);
        return convertToDto(savedCourse);
    }

    @Transactional
    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));

        existingCourse.setName(courseDto.getName());
        existingCourse.setDescription(courseDto.getDescription());
        existingCourse.setLevel(courseDto.getLevel());

        Course updatedCourse = courseRepository.save(existingCourse);
        return convertToDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khóa học với ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    // Hàm tiện ích để chuyển đổi từ Entity sang DTO
    private CourseDto convertToDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setLevel(course.getLevel());
        return dto;
    }
}