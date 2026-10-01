package com.chrus.nextdate.controller;

import com.chrus.nextdate.dto.UserResponse;
import com.chrus.nextdate.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserResponse response = UserResponse.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .build();

        return ResponseEntity.ok(response);
    }
}
