package com.sonet.core.controller;

import com.sonet.core.model.entity.User;
import com.sonet.core.repository.UserReadOnlyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AuthController", description = "Контроллер авторизации")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private final UserReadOnlyRepository userReadOnlyRepository;


    @GetMapping("/user/uuid/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Посмотреть профиль")
    public User getUserById(@PathVariable("uuid") UUID userId) {
        return userReadOnlyRepository
                .findByUuid(userId, true)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + userId));
    }

    @GetMapping("/user/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Посмотреть профиль")
    public User getUserById(@PathVariable("email") String email) {
        return userReadOnlyRepository
                .findByEmail(email, true)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + email));
    }
}
