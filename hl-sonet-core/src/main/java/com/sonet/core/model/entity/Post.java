package com.sonet.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.UUID;

@Table("posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    private Long id;

    @NotNull
    private UUID uuid;

    @NotNull
    private Integer userId;

    @NotNull
    private UUID userUuid;

    @NotBlank
    private String message;

    @NotBlank
    private String userFirstName;

    private String userLastName;

    @Past
    @NotNull
    private Date createdDate;

    @Past
    private Date updatedDate;
}
