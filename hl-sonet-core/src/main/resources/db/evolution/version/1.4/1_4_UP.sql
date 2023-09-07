create table if not exists shards (
    shard_id integer primary key,
    created_date timestamp without time zone not null default (now() at time zone 'utc')
);

create table if not exists dialog_shards (
    key_hash integer primary key,
    shard_id integer not null references shards (shard_id),
    resharding boolean default false,
    created_date timestamp without time zone not null default (now() at time zone 'utc')
);

/*create table if not exists dialog_messages (
    uuid uuid primary key default uuid_generate_v4(),
    key_hash integer references dialog_shards(key_hash),
    shard_id integer not null, -- not constraint here for resharding
    from_user_id integer references users(id) on delete cascade,
    to_user_id integer references users(id) on delete cascade,
    message text not null,
    created_date timestamp without time zone not null default (now() at time zone 'utc')
);*/

insert into shards (shard_id)
values (1), (2);

create EXTENSION postgres_fdw;

--------- server 1 -------------
create server messages_1_server
    FOREIGN DATA WRAPPER  postgres_fdw
    OPTIONS (host 'pgshard1', port '5432', dbname 'postgres');

create user mapping for postgres
    server messages_1_server
    options (user 'postgres', password 'pass');

create foreign table dialog_messages_1 (
    uuid uuid default uuid_generate_v4(),
    key_hash integer not null ,
    shard_id integer not null, -- not constraint here for resharding
    from_user_id integer not null ,
    to_user_id integer not null ,
    message text not null,
    created_date timestamp without time zone not null default (now() at time zone 'utc')
    )
    server messages_1_server
    options (schema_name 'public', table_name 'dialog_messages');

--------- server 2 -------------
create server messages_2_server
    FOREIGN DATA WRAPPER  postgres_fdw
    OPTIONS (host 'pgshard2', port '5432', dbname 'postgres');

create user mapping for postgres
    server messages_2_server
    options (user 'postgres', password 'pass');

create foreign table dialog_messages_2 (
    uuid uuid default uuid_generate_v4(),
    key_hash integer not null ,
    shard_id integer not null, -- not constraint here for resharding
    from_user_id integer not null ,
    to_user_id integer not null ,
    message text not null,
    created_date timestamp without time zone not null default (now() at time zone 'utc')
    )
    server messages_2_server
    options (schema_name 'public', table_name 'dialog_messages');
--------------------------------
create view dialog_messages AS
select * from dialog_messages_1
union all
select * from dialog_messages_2;
--------------------------------
create rule dialog_messages_insert as on insert to dialog_messages do instead nothing ;
create rule dialog_messages_update as on update to dialog_messages do instead nothing ;
create rule dialog_messages_delete as on delete to dialog_messages do instead nothing ;

create rule dialog_messages_insert_to_1 as on insert to dialog_messages
    where (shard_id = 1)
    do instead insert into dialog_messages_1 values (NEW.*);

create rule dialog_messages_insert_to_2 as on insert to dialog_messages
    where (shard_id = 2)
    do instead insert into dialog_messages_2 values (NEW.*);