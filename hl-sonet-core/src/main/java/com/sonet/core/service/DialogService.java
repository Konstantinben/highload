package com.sonet.core.service;

import com.sonet.core.model.dto.DialogMessageDto;
import com.sonet.core.model.entity.DialogMessage;
import com.sonet.core.model.entity.DialogShard;
import com.sonet.core.model.entity.User;
import com.sonet.core.model.mapper.DialogMessageMapper;
import com.sonet.core.repository.DialogMessageRepository;
import com.sonet.core.repository.DialogShardRepository;
import com.sonet.core.repository.UserReadRepository;
import com.sonet.core.security.UserSessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogService {

    private final UserSessionUtil userSessionUtil;
    private final UserReadRepository userReadRepository;

    private final DialogShardRepository dialogShardRepository;

    private final DialogMessageRepository dialogMessageRepository;

    private final DialogMessageMapper dialogMessageMapper;

    public List<DialogMessageDto> getDialog(UUID toUserUuid) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = userReadRepository.findByUuid(toUserUuid).orElseThrow(
                () -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid)
        );
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        var messages = dialogMessageRepository.getByKeyHashOrderByCreatedDateDesc(keyHash);

        return dialogMessageMapper.toDtoList(messages, fromUser, toUser);
    }

    public DialogMessageDto sendMessage(UUID toUserUuid, String message) {
        User fromUser = userSessionUtil.getAuthorizedUser();
        User toUser = userReadRepository.findByUuid(toUserUuid).orElseThrow(
                () -> new BadCredentialsException("Cannot find user by uuid" + toUserUuid)
        );
        int keyHash = calculateHashKey(fromUser.getUuid(), toUserUuid);
        DialogShard dialogShard = findOrCreatedDialogShard(keyHash);
        UUID uuid = UUID.randomUUID();
        dialogMessageRepository.create(uuid, keyHash, fromUser.getId(), toUser.getId(), dialogShard.getShardId(), message, new Date());
        DialogMessage dialogMessage = dialogMessageRepository.getByKeyHashOrderByCreatedDateDesc(keyHash).get(0);
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
