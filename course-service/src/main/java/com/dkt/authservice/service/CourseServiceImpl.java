package com.dkt.authservice.service;

import com.dkt.authservice.dto.CourseDto;
import com.dkt.authservice.entity.Course;
import com.dkt.authservice.repository.CourseRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final MessageSource messageSource;
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository repo,
                         MessageSource msg) {
        this.courseRepository = repo;
        this.messageSource = msg;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage("error.course.notfound", new Object[]{id}, LocaleContextHolder.getLocale());
                    return new RuntimeException(msg);
                });
    }

    @Override
    @Transactional
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setLevel(courseDto.getLevel());

        Course savedCourse = courseRepository.save(course);
        return convertToDto(savedCourse);
    }

    @Override
    @Transactional
    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage("error.course.notfound", new Object[]{id}, LocaleContextHolder.getLocale());
                    return new RuntimeException(msg);
                });

        existingCourse.setName(courseDto.getName());
        existingCourse.setDescription(courseDto.getDescription());
        existingCourse.setLevel(courseDto.getLevel());

        Course updatedCourse = courseRepository.save(existingCourse);
        return convertToDto(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            String msg = messageSource.getMessage("error.course.notfound", new Object[]{id}, LocaleContextHolder.getLocale());
            throw new RuntimeException(msg);
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