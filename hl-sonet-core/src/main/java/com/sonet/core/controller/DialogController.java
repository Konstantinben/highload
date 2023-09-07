package com.sonet.core.controller;

import com.sonet.core.model.dto.DialogMessageDto;
import com.sonet.core.model.dto.MessageDto;
import com.sonet.core.model.dto.PostDto;
import com.sonet.core.service.DialogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "DialogController", description = "Контроллер диалогов")
@RestController
@RequestMapping("/dialog")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @PostMapping("/{toUuid}/send")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Отправить сообщение")
    public DialogMessageDto add(@PathVariable("toUuid") UUID toUserUuid, @RequestBody @Valid MessageDto messageDto) {
        return dialogService.sendMessage(toUserUuid, messageDto.getMessage());
    }

    @GetMapping("/{toUuid}/get")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Посмотреть диалог")
    public List<DialogMessageDto> getByUuid(@PathVariable String toUuid) {
        return dialogService.getDialog(UUID.fromString(toUuid));
    }
}
