package com.dkt.userservice.service;

import com.dkt.userservice.dto.*;
import com.dkt.userservice.entity.Role;
import com.dkt.userservice.entity.Student;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.entity.UserRole;
import com.dkt.userservice.repository.RoleRepository;
import com.dkt.userservice.repository.StudentRepository;
import com.dkt.userservice.repository.UserRepository;
import com.dkt.userservice.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository uRepo,
                       RoleRepository rRepo,
                       UserRoleRepository urRepo,
                       PasswordEncoder encoder,
                       StudentRepository sRepo) {
        this.userRepository = uRepo;
        this.roleRepository = rRepo;
        this.userRoleRepository = urRepo;
        this.passwordEncoder = encoder;
        this.studentRepository = sRepo;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email đã được sử dụng!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Role userRole = roleRepository.findByName("ROLE_HOC_VIEN")
                .orElseThrow(() -> new RuntimeException("Lỗi: Role HOC_VIEN không tồn tại."));

        UserRole userRoleRelation = new UserRole(savedUser.getId(), userRole.getId());
        userRoleRepository.save(userRoleRelation);

        return savedUser;
    }
    public User findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với email: " + email));
        } catch (RuntimeException e) {
            return User.builder()
                    .id(-1L)
                    .email(email)
                    .code("404")
                    .message("Khong tim thay user")
                    .build();
        }

    }

    // Hàm này lấy Role
    public List<Role> findRolesByUserId(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        // Trích xuất danh sách roleId từ kết quả
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId) // Sử dụng method reference cho ngắn gọn
                .toList();
        // Tìm tất cả các Role từ danh sách ID
        return roleRepository.findAllById(roleIds);
    }

    @Transactional
    public User updateUserProfile(String email, UpdateProfileRequest request) {
        User user = findUserByEmail(email);

        // Chỉ cập nhật fullName nếu client có gửi nó lên (khác null)
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserPassword(String email, ChangePasswordRequest request) {
        User user = findUserByEmail(email);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Mật khẩu cũ không chính xác.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUserAccount(String email) {
        User user = findUserByEmail(email);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserDto convertToDto(User user) {
        List<Role> roles = findRolesByUserId(user.getId());
        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRoles(roleNames);

        return dto;
    }

    public StudentProfileDto findStudentProfileByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId) // <-- Thêm method này vào StudentRepository
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ học viên cho user ID: " + userId));

        return convertToDto(student); // Thêm hàm tiện ích convertToDto
    }

    private StudentProfileDto convertToDto(Student student) {
        StudentProfileDto dto = new StudentProfileDto();
        dto.setId(student.getId());
        dto.setUserId(student.getUserId());
        dto.setStudentCode(student.getStudentCode());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setAddress(student.getAddress());
        return dto;
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
    }
    public UserSimpleDto findSimpleUserByEmail(String email) {
        User user = findUserByEmail(email); // Tái sử dụng hàm đã có
        return new UserSimpleDto(user.getId(), user.getEmail(), user.getFullName());
    }


}