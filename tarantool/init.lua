box.cfg {

        listen = 'localhost:3301';
        io_collect_interval = nil;
        readahead = 16320;
        memtx_memory = 128 * 1024 * 1024; -- 128Mb
        memtx_min_tuple_size = 16;
        memtx_max_tuple_size = 128 * 1024 * 1024; -- 128Mb
        vinyl_memory = 128 * 1024 * 1024; -- 128Mb
        vinyl_cache = 128 * 1024 * 1024; -- 128Mb
        vinyl_max_tuple_size = 128 * 1024 * 1024; -- 128Mb
        vinyl_write_threads = 2;
        wal_mode = "none";
        wal_max_size = 256 * 1024 * 1024;
        checkpoint_interval = 60 * 60; -- one hour
        checkpoint_count = 6;
        force_recovery = true;
        log_level = 5;
        log_nonblock = false;
        too_long_threshold = 0.5;
}

local function bootstrap()
        local space = box.schema.create_space('example')
        space:create_index('primary')
        box.schema.user.grant('guest', 'read,write,execute', 'universe')
end

-- for first run create a space and add set up grants
box.once('example-1.0', bootstrap)
box.once('schema', start)

function start()
    uuid = require('uuid')

    local dialogs = box.schema.space.create('dialog_messages', { if_not_exists = true })
    dialogs:format({
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
end

function getByKeyHashOrderByCreatedDateDesc(key_hash)
    return box.space.dialog_messages.index.key_hash_created_date_idx:select({ key_hash }, { iterator = 'LE' });
end

function addDialog(keyHash, fromUserId, toUserId, shardId, message, createdDate)
    return box.space.dialog_messages:insert{uuid.new(), keyHash, fromUserId, toUserId, shardId, message, createdDate};
end
