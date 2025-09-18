package com.dkt.authservice.service;

import com.dkt.authservice.dto.ClassroomDto;
import com.dkt.authservice.dto.EnrollmentRequest;
import com.dkt.authservice.entity.Classroom;
import com.dkt.authservice.entity.ClassroomStudent;
import com.dkt.authservice.entity.ClassroomStudentId;
import com.dkt.authservice.repository.ClassroomRepository;
import com.dkt.authservice.repository.ClassroomStudentRepository;
import com.dkt.authservice.repository.CourseRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomStudentRepository classroomStudentRepository;
    private final CourseRepository courseRepository;
    private final MessageSource messageSource;

    public ClassroomServiceImpl(ClassroomRepository classRepo,
                                ClassroomStudentRepository csRepo,
                                CourseRepository courseRepo,
                                MessageSource msgSrc) {
        this.classroomRepository = classRepo;
        this.classroomStudentRepository = csRepo;
        this.courseRepository = courseRepo;
        this.messageSource = msgSrc;
    }

    @Override
    public List<ClassroomDto> getAllClassrooms() {
        return classroomRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClassroomDto getClassroomById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.classroom.notfound", new Object[]{id}, LocaleContextHolder.getLocale())
                ));
        return convertToDto(classroom);
    }

    @Override
    @Transactional
    public ClassroomDto createClassroom(ClassroomDto classroomDto) {
        courseRepository.findById(classroomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.course.notfound", new Object[]{classroomDto.getCourseId()}, LocaleContextHolder.getLocale())
                ));

        Classroom classroom = new Classroom();
        fromDto(classroom, classroomDto);

        Classroom savedClassroom = classroomRepository.save(classroom);
        return convertToDto(savedClassroom);
    }

    @Override
    @Transactional
    public ClassroomDto updateClassroom(Long id, ClassroomDto classroomDto) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.classroom.notfound", new Object[]{id}, LocaleContextHolder.getLocale())
                ));

        courseRepository.findById(classroomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.course.notfound", new Object[]{classroomDto.getCourseId()}, LocaleContextHolder.getLocale())
                ));

        fromDto(existingClassroom, classroomDto);

        Classroom updatedClassroom = classroomRepository.save(existingClassroom);
        return convertToDto(updatedClassroom);
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException(
                    messageSource.getMessage("error.classroom.notfound", new Object[]{id}, LocaleContextHolder.getLocale())
            );
        }
        classroomRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void enrollStudents(EnrollmentRequest request) {
        Long classroomId = request.getClassroomId();
        classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.classroom.notfound", new Object[]{classroomId}, LocaleContextHolder.getLocale())
                ));

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