box.schema.space.create('dialog_messages', { if_not_exists = true })
box.space.dialog_messages:format({
        { name = 'uuid', type = 'uuid' },
        { name = 'key_hash', type = 'number' },
        { name = 'from_user_id', type = 'number' },
        { name = 'to_user_id', type = 'number' },
        { name = 'message', type = 'string' },
        { name = 'created_date', type = 'string' }
    })
box.space.dialog_messages:create_index('primary', { type = "TREE", unique = true, parts = { 1, 'uuid' }, if_not_exists = true})
--box.space.dialog_messages:create_index('key_hash_created_date_idx', { type = "TREE", unique = true, parts = { 2, 'number', 6, 'string' }, if_not_exists = true })
box.space.dialog_messages:create_index('key_hash_created_date_idx', { type = "TREE", unique = true, parts = { 2, 'number', 6, 'string', 1, 'uuid'}, if_not_exists = true })

uuid = require('uuid')

function getByKeyHashOrderByCreatedDateDesc(key_hash)
    return box.space.dialog_messages.index.key_hash_created_date_idx:select({ key_hash }, { iterator = 'LE' });
end

function addDialog(keyHash, fromUserId, toUserId, shardId, message, createdDate)
    return box.space.dialog_messages:insert{uuid.new(), keyHash, fromUserId, toUserId, shardId, message, createdDate};
end

-- check scripts
box.space.dialog_messages:insert({uuid.fromstr('8277e588-110e-418f-900c-7e5d496c3e84'), 1122496891, 4, 2, 'Request 1-> 4', '2023-09-23T15:22:39.977Z'})
box.space.dialog_messages:insert({uuid.new(), 1122496891, 4, 2, 'Request 1-> 4', '2023-09-23T15:23:39.977Z'})
addDialog(1122496891, 4, 2, 'Request 1-> 4', '2023-09-23T15:24:39.977Z')
getByKeyHashOrderByCreatedDateDesc(1122496891)
box.space.dialog_messages:truncate()