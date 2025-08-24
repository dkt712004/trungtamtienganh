package com.dkt.userservice.service;

import com.dkt.userservice.dto.ChangePasswordRequest;
import com.dkt.userservice.dto.RegisterRequest;
import com.dkt.userservice.dto.UpdateProfileRequest;
import com.dkt.userservice.dto.UserDto;
import com.dkt.userservice.entity.Role;
import com.dkt.userservice.entity.User;
import com.dkt.userservice.entity.UserRole;
import com.dkt.userservice.repository.RoleRepository;
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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository uRepo, RoleRepository rRepo, UserRoleRepository urRepo, PasswordEncoder encoder) {
        this.userRepository = uRepo;
        this.roleRepository = rRepo;
        this.userRoleRepository = urRepo;
        this.passwordEncoder = encoder;
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

    // Hàm này sẽ lấy Role đơn giản hơn
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
        user.setFullName(request.getFullName());
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
}