package com.sonet.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Table("dialog_shards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DialogShard {

    @Id
    private int keyHash;

    private int shardId;

    private boolean resharding;
}
