package com.dkt.authservice.security;

import com.dkt.authservice.entity.Role;
import com.dkt.authservice.entity.User;
import com.dkt.authservice.entity.UserRole;
import com.dkt.authservice.repository.RoleRepository;
import com.dkt.authservice.repository.UserRepository;
import com.dkt.authservice.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    // Sửa lại constructor để nhận thêm UserRoleRepository
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm user trong DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        // --- LOGIC MỚI ĐỂ LẤY VAI TRÒ ---
        // 2. Dùng userId để tìm tất cả các bản ghi user-role tương ứng
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());

        // 3. Từ các bản ghi user-role, trích xuất ra danh sách các roleId
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        // 4. Dùng danh sách roleId để tìm tất cả các đối tượng Role
        List<Role> roles = roleRepository.findAllById(roleIds);

        // 5. Chuyển đổi danh sách Role thành danh sách GrantedAuthority mà Spring Security cần
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        // --- KẾT THÚC LOGIC MỚI ---

        // 6. Trả về đối tượng UserDetails hoàn chỉnh
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities // <-- Danh sách quyền được lấy thủ công
        );
    }
}