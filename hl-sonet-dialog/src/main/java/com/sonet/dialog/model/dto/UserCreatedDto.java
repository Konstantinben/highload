package com.sonet.dialog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserCreatedDto {

    @NotNull
    private UUID accountId;
}
