package com.sonet.core.repository;


import com.sonet.core.model.entity.DialogShard;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DialogShardRepository extends CrudRepository<DialogShard, Integer> {

    DialogShard getByKeyHash(int keyHash);

    @Query(value = "select shard_id from shards")
    List<Integer> getAllShards();

    @Query(value = """
            insert into dialog_shards (key_hash, shard_id)
            values (:keyHash, :shardId)
            returning key_hash, shard_id, resharding, created_date
            """)
    DialogShard save(int keyHash, int shardId);
}
