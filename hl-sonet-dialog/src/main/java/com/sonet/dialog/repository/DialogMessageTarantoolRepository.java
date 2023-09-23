package com.sonet.dialog.repository;

import com.sonet.dialog.model.entity.DialogMessage;
import io.tarantool.driver.api.TarantoolClient;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class DialogMessageTarantoolRepository {

    private final TarantoolClient tarantoolClient;

    @SneakyThrows
    public DialogMessage create(int keyHash, Integer fromUserId, Integer toUserId, String message, Date createdDate) {
        //Date.from(Instant.parse(new Date().toInstant().toString()));
        //2023-09-23T15:22:39.977Z
        //addDialog(1122496891, 4, 2, 'Request 1-> 4', '2023-09-23T15:24:39.977Z')
        var result = tarantoolClient.call("addDialog", List.of(keyHash, fromUserId, toUserId, message, createdDate.toInstant().toString())).get(15, TimeUnit.SECONDS);
        if (CollectionUtils.isNotEmpty(result)) {
            return mapListToDialogMessage().apply(((List<List>) result.get(0)));
        }
        return null;
    }


    @SneakyThrows
    public List<DialogMessage> getByKeyHashOrderByCreatedDateDesc(int keyHash) {
        var result = tarantoolClient.call("getByKeyHashOrderByCreatedDateDesc", List.of(keyHash)).get(15, TimeUnit.SECONDS);
        if (CollectionUtils.isNotEmpty(result)) {
            return ((List<List>) result.get(0)).stream()
                    .map(mapListToDialogMessage())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static Function<List, DialogMessage> mapListToDialogMessage() {
        return list -> DialogMessage.builder()
                .uuid((UUID) list.get(0))
                .keyHash((Integer) list.get(1))
                .fromUserId((Integer) list.get(2))
                .toUserId((Integer) list.get(3))
                .message((String) list.get(4))
                .createdDate(Date.from(Instant.parse((String) list.get(5))))
                .build();
    }
}
