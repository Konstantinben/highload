package com.sonet.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class DialogMessageDto {
    @NotNull
    private UUID uuid;

    @NotNull
    private UUID fromUserUuid;

    @NotNull
    private String fromFirstName;

    private String fromLastName;

    @NotNull
    private UUID toUserUuid;

    @NotNull
    private String toFirstName;

    private String toUserLastName;

    @NotNull
    private String message;

    @Past
    @NotNull
    private Date createdDate;
}
