package com.sonet.dialog.repository;

import com.sonet.dialog.model.entity.DialogMessage;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DialogMessageRepository extends CrudRepository<DialogMessage, UUID> {

    List<DialogMessage> getByKeyHashOrderByCreatedDateDesc(int keyHash);

    Optional<DialogMessage> getFirstByKeyHashOrderByCreatedDateDesc(int keyHash);

    @Modifying
    @Query("""
            insert into dialog_messages (uuid, key_hash, shard_id, from_user_id, to_user_id, message, created_date)
            values (:uuid, :keyHash, :fromUserId, :toUserId, :shardId, :message, :createdDate)
            """)
    void create(UUID uuid, int keyHash, Integer fromUserId, Integer toUserId, int shardId, String message, Date createdDate);
}
