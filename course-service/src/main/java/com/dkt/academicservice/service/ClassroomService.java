package com.dkt.academicservice.service;

import com.dkt.academicservice.dto.ClassroomDto;
import com.dkt.academicservice.dto.EnrollmentRequest;
import com.dkt.academicservice.entity.Classroom;
import com.dkt.academicservice.entity.ClassroomStudent;
import com.dkt.academicservice.entity.ClassroomStudentId;
import com.dkt.academicservice.repository.ClassroomRepository;
import com.dkt.academicservice.repository.ClassroomStudentRepository;
import com.dkt.academicservice.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomStudentRepository classroomStudentRepository;
    private final CourseRepository courseRepository;

    public ClassroomService(ClassroomRepository classroomRepo,
                            ClassroomStudentRepository csRepo,
                            CourseRepository courseRepo) {
        this.classroomRepository = classroomRepo;
        this.classroomStudentRepository = csRepo;
        this.courseRepository = courseRepo;
    }

    public List<ClassroomDto> getAllClassrooms() {
        return classroomRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ClassroomDto getClassroomById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ID: " + id));
        return convertToDto(classroom);
    }

    @Transactional
    public ClassroomDto createClassroom(ClassroomDto classroomDto) {
        courseRepository.findById(classroomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khóa học với ID " + classroomDto.getCourseId() + " không tồn tại."));

        // TODO: Nâng cao - Gọi sang user-service để kiểm tra teacherId có tồn tại và có vai trò GIAO_VIEN không.

        Classroom classroom = new Classroom();
        fromDto(classroom, classroomDto);

        Classroom savedClassroom = classroomRepository.save(classroom);
        return convertToDto(savedClassroom);
    }

    @Transactional
    public ClassroomDto updateClassroom(Long id, ClassroomDto classroomDto) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ID: " + id));

        courseRepository.findById(classroomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khóa học với ID " + classroomDto.getCourseId() + " không tồn tại."));

        fromDto(existingClassroom, classroomDto);

        Classroom updatedClassroom = classroomRepository.save(existingClassroom);
        return convertToDto(updatedClassroom);
    }

    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lớp học với ID: " + id);
        }
        // Việc xóa lớp học sẽ tự động xóa các bản ghi ghi danh nhờ `ON DELETE CASCADE` trong DB
        classroomRepository.deleteById(id);
    }

    @Transactional
    public void enrollStudents(EnrollmentRequest request) {
        Long classroomId = request.getClassroomId();
        classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Lớp học với ID " + classroomId + " không tồn tại."));

        // TODO: Nâng cao - Gọi sang user-service để kiểm tra danh sách studentIds có hợp lệ không.

        List<ClassroomStudent> enrollments = request.getStudentIds().stream().map(studentId -> {
            ClassroomStudentId csId = new ClassroomStudentId();
            csId.setClassroomId(classroomId);
            csId.setStudentId(studentId);

            ClassroomStudent enrollment = new ClassroomStudent();
            enrollment.setId(csId);
            enrollment.setEnrollmentDate(LocalDateTime.now());

            return enrollment;
        }).collect(Collectors.toList());

        classroomStudentRepository.saveAll(enrollments);
    }

    private ClassroomDto convertToDto(Classroom classroom) {
        ClassroomDto dto = new ClassroomDto();
        dto.setId(classroom.getId());
        dto.setName(classroom.getName());
        dto.setCourseId(classroom.getCourseId());
        dto.setTeacherId(classroom.getTeacherId());
        dto.setStartDate(classroom.getStartDate());
        dto.setEndDate(classroom.getEndDate());
        dto.setScheduleInfo(classroom.getScheduleInfo());
        return dto;
    }

    private void fromDto(Classroom classroom, ClassroomDto dto) {
        classroom.setName(dto.getName());
        classroom.setCourseId(dto.getCourseId());
        classroom.setTeacherId(dto.getTeacherId());
        classroom.setStartDate(dto.getStartDate());
        classroom.setEndDate(dto.getEndDate());
        classroom.setScheduleInfo(dto.getScheduleInfo());
    }
}