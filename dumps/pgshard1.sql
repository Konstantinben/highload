-- create table news (
--   id bigint not null,
--   shard_id int not null,
--   CONSTRAINT news_shard_id_check CHECK (shard_id=1),
--   message text not null
-- );
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists dialog_messages (
   uuid uuid primary key default uuid_generate_v4(),
   key_hash integer not null,
   shard_id integer not null,
   CONSTRAINT shard_id_check CHECK (shard_id=1),
   from_user_id integer not null,
   to_user_id integer not null,
   message text not null,
   created_date timestamp without time zone not null default (now() at time zone 'utc')
);