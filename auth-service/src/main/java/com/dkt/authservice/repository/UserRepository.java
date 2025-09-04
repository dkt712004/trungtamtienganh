package com.dkt.authservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.dkt.authservice.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Lấy roles của user thông qua bảng trung gian user_roles
    @Query(value = "SELECT r.name FROM roles r " +
            "INNER JOIN user_roles ur ON ur.role_id = r.id " +
            "INNER JOIN users u ON u.id = ur.user_id " +
            "WHERE u.email = ?1", nativeQuery = true)
    List<String> findRolesByEmail(String email);
}
