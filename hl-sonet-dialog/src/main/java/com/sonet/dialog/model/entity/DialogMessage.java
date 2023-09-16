package com.sonet.dialog.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.UUID;

@Table("dialog_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogMessage {

    @Id
    private UUID uuid;

    @NotNull
    private Integer keyHash;

    @NotNull
    private Integer shardId;

    @NotNull
    private Integer fromUserId;

    @NotNull
    private Integer toUserId;

    @NotNull
    private String message;

    @Past
    @NotNull
    private Date createdDate;
}
