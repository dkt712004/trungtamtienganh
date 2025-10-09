package com.dkt.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.dkt.authservice.client.StudentProfileDto;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/by-email")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/{userId}/student-profile")
    StudentProfileDto getStudentProfileByUserId(@PathVariable("userId") Long userId);

}

