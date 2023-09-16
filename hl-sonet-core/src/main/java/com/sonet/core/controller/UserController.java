package com.sonet.core.controller;

import com.sonet.core.model.ResponseUtil;
import com.sonet.core.model.dto.AuthenticationRequestDto;
import com.sonet.core.model.dto.SignupRequestDto;
import com.sonet.core.model.dto.UserCreatedDto;
import com.sonet.core.model.dto.UserProfileDto;
import com.sonet.core.model.entity.User;
import com.sonet.core.model.mapper.UserMapper;
import com.sonet.core.repository.UserReadOnlyRepository;
import com.sonet.core.repository.UserRepository;
import com.sonet.core.security.JwtTokenProvider;
import com.sonet.core.security.UserSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "UserController", description = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final UserSessionUtil userSessionUtil;
    private final UserRepository userRepository;

    private final UserReadOnlyRepository userReadOnlyRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;


    @PostMapping("/register")
    @Operation(summary = "Зарегистрироваться")
    public UserCreatedDto signup(@RequestBody @Valid SignupRequestDto userSignupReq) {
        userReadOnlyRepository.findByEmail(userSignupReq.getEmail(), true).ifPresent(
                existentUser -> {throw new RuntimeException("User " + userSignupReq.getEmail() + " already registered.");}
        );

        User user = userMapper.toUser(userSignupReq, passwordEncoder.encode(userSignupReq.getPassword()));
        userRepository.save(user);
        return new UserCreatedDto(user.getUuid());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Изменить профиль")
    public UserProfileDto updateProfile(@RequestBody @Valid UserProfileDto profile) {
        User user = userSessionUtil.getAuthorizedUser();

        userRepository.save(userMapper.toUser(user, profile));

        return userMapper.toUserProfileDto(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Получить JWT token")
    public ResponseEntity<?> create(@RequestBody @Valid AuthenticationRequestDto authRequest) {

        String email = authRequest.getEmail();
        String password = authRequest.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User does not exists"));
            String token = jwtTokenProvider.createToken(email, user.getRole(), user.getUuid());

            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            return ResponseUtil.error("HttpStatus.FORBIDDEN", "Invalid Email/password combination", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    @GetMapping ("/get/{uuid}")
    @Operation(summary = "Посмотреть профиль")
    public UserProfileDto getById(@PathVariable("uuid") UUID userId) {
        return userReadOnlyRepository
                .findByUuid(userId, false)
                .map(userMapper::toUserProfileDto)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + userId));
    }


    @GetMapping ("/search")
    @Operation(summary = "Поиск анкет")
    public List<UserProfileDto> searchByName(@NotBlank @RequestParam("first_name") String firstName,
                                             @NotBlank @RequestParam("last_name") String lastName) {
        List<UserProfileDto> result = userReadOnlyRepository
                .findLikeFirstAndLastNames(firstName.trim(), lastName.trim())
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .map(userMapper::toUserProfileDto)
                .collect(Collectors.toList());

        //log.debug("/user/search found " + result.size() + " records by " + firstName + " and " + lastName);
        return result;
    }
}


