package com.sonet.dialog.service;

import com.sonet.dialog.model.entity.DialogMessage;
import com.sonet.dialog.model.entity.DialogShard;
import com.sonet.dialog.model.entity.User;

import com.sonet.dialog.client.AuthServiceClient;
import com.sonet.dialog.security.AdminJwtTokenProvider;
import com.sonet.dialog.security.UserSessionUtil;
import com.sonet.dialog.model.dto.DialogMessageDto;
import com.sonet.dialog.model.mapper.DialogMessageMapper;
import com.sonet.dialog.repository.DialogMessageRepository;
import com.sonet.dialog.repository.DialogShardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

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

    private final DialogMessageMapper dialogMessageMapper;

    public List<DialogMessageDto> getDialog(UUID toUserUuid) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = Optional.ofNullable(authServiceClient.getUserById(toUserUuid, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid));
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        var messages = dialogMessageRepository.getByKeyHashOrderByCreatedDateDesc(keyHash);

        return dialogMessageMapper.toDtoList(messages, fromUser, toUser);
    }

    public DialogMessageDto sendMessage(UUID toUserUuid, String message) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = Optional.ofNullable(authServiceClient.getUserById(toUserUuid, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid));
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        DialogShard dialogShard = findOrCreatedDialogShard(keyHash);
        UUID uuid = UUID.randomUUID();

        callLua(uuid, keyHash, fromUser.getId(), toUser.getId(), message, new Date());

        dialogMessageRepository.create(uuid, keyHash, fromUser.getId(), toUser.getId(), dialogShard.getShardId(), message, new Date());
        DialogMessage dialogMessage = dialogMessageRepository.getByKeyHashOrderByCreatedDateDesc(keyHash).get(0);


        return dialogMessageMapper.toDto(dialogMessage, fromUser, toUser);
    }

    private void callLua(UUID uuid, int keyHash, Integer fromUserId, Integer toUserId, String message, Date createdDate) {


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
