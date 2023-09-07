package com.sonet.core.controller;

import com.sonet.core.repository.UserRepository;
import com.sonet.core.security.UserSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "FriendController", description = "Контроллер списка друзей")
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final UserSessionUtil userSessionUtil;
    private final UserRepository userRepository;

    @PutMapping("/set/{user_id}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Добавить в друзья")
    public UUID add(@PathVariable("user_id") UUID friendUuid) {
        userRepository.insertFriend(userSessionUtil.getAuthorizedUser().getId(), friendUuid);
        return friendUuid;
    }

    @DeleteMapping("/delete/{user_id}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Удалить из друзей")
    public UUID delete(@PathVariable("user_id") UUID friendUuid) {
        userRepository.deleteFriend(userSessionUtil.getAuthorizedUser().getId(), friendUuid);
        return friendUuid;
    }
}
