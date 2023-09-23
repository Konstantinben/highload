package com.sonet.dialog.service;

import com.sonet.dialog.client.AuthServiceClient;
import com.sonet.dialog.model.dto.DialogMessageDto;
import com.sonet.dialog.model.entity.DialogMessage;
import com.sonet.dialog.model.entity.DialogShard;
import com.sonet.dialog.model.entity.User;
import com.sonet.dialog.model.mapper.DialogMessageMapper;
import com.sonet.dialog.model.tarantool.DialogMessageTarantool;
import com.sonet.dialog.repository.DialogMessageRepository;
import com.sonet.dialog.repository.DialogMessageTarantoolRepository;
import com.sonet.dialog.repository.DialogShardRepository;
import com.sonet.dialog.security.AdminJwtTokenProvider;
import com.sonet.dialog.security.UserSessionUtil;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogService {

    private final UserSessionUtil userSessionUtil;
    private final AuthServiceClient authServiceClient;

    private final AdminJwtTokenProvider adminJwtTokenProvider;

    private final DialogShardRepository dialogShardRepository;

    private final DialogMessageRepository dialogMessageRepository;

    private final DialogMessageTarantoolRepository dialogMessageTarantoolRepository;

    private final DialogMessageMapper dialogMessageMapper;

    @SneakyThrows
    public List<DialogMessageDto> getDialog(UUID toUserUuid) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = Optional.ofNullable(authServiceClient.getUserById(toUserUuid, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid));
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        var tarantoolMessages = dialogMessageTarantoolRepository.getByKeyHashOrderByCreatedDateDesc(keyHash);
        return dialogMessageMapper.toDtoList(tarantoolMessages, fromUser, toUser);
        //var messages = dialogMessageRepository.getByKeyHashOrderByCreatedDateDesc(keyHash);
        //return dialogMessageMapper.toDtoList(messages, fromUser, toUser);
    }

    public DialogMessageDto sendMessage(UUID toUserUuid, String message) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = Optional.ofNullable(authServiceClient.getUserById(toUserUuid, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid));
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        //DialogShard dialogShard = findOrCreatedDialogShard(keyHash);
        UUID uuid = UUID.randomUUID();

//        dialogMessageRepository.create(uuid, keyHash, fromUser.getId(), toUser.getId(), dialogShard.getShardId(), message, new Date());
//        DialogMessage dialogMessage = dialogMessageRepository
//                .getFirstByKeyHashOrderByCreatedDateDesc(keyHash)
//                .orElseThrow(() -> new RuntimeException("Cannot find dialog by keyHash: " + keyHash));
        var dialogMessage = dialogMessageTarantoolRepository.create(keyHash, fromUser.getId(), toUser.getId(), message, new Date());

        return dialogMessageMapper.toDto(dialogMessage, fromUser, toUser);
    }

    public DialogShard findOrCreatedDialogShard(int keyHash) {
        DialogShard shard = dialogShardRepository.getByKeyHash(keyHash);
        if (shard == null) {
            List<Integer> shardIds = dialogShardRepository.getAllShards();
            Integer shardId = shardIds.get(new Random().nextInt(shardIds.size()));
            shard = dialogShardRepository.save(keyHash, shardId);
        }
        return shard;
    }

    public Integer calculateHashKey(UUID firstUserUuid, UUID secondUserUuid) {
        return List.of(firstUserUuid, secondUserUuid).stream()
                .map(UUID::toString)
                .sorted()
                .collect(Collectors.joining()).hashCode();
    }
}
