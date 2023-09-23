/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sonet.dialog.model.tarantool;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;
import org.springframework.data.tarantool.core.mapping.Tuple;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Tuple("dialog_messages")
public class DialogMessageTarantool implements Serializable {

    @Id
    private UUID uuid;

    @Field(name = "bucket_id")
    private Integer bucketId;

    @Field(name = "key_hash")
    @NotEmpty
    private Integer keyHash;

    @Field(name = "from_user_id")
    @NotNull
    private Integer fromUserId;

    @Field(name = "to_user_id")
    @NotNull
    private Integer toUserId;

    @Field(name = "message")
    @NotEmpty
    private String message;

    @Past
    @Field(name = "created_date")
    @NotNull
    private Date createdDate;

    public boolean isNew() {
        return this.uuid == null;
    }

}
