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

@Service // Đánh dấu đây là một Spring Bean
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("..."));

        // Logic "join" thủ công để lấy vai trò
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        List<Role> roles = roleRepository.findAllById(roleIds);

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // TẠO RA một đối tượng UserDetails mới từ thông tin của User Entity
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(), // isEnabled
                true, // isAccountNonExpired
                true, // isCredentialsNonExpired
                true, // isAccountNonLocked
                authorities
        );
    }
}