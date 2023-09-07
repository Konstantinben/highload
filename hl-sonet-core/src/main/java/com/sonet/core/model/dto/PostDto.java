package com.sonet.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class PostDto {

    private UUID uuid;

    private UUID userUuid;

    @NotBlank
    private String message;

    private String userFirstName;

    private String userLastName;

    @Past
    private Date createdDate;

    @Past
    private Date updatedDate;
}
