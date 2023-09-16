drop server if exists messages_1_server CASCADE;
drop server if exists messages_2_server CASCADE ;

drop EXTENSION postgres_fdw;

drop table if exists dialog_messages;
drop table if exists dialog_shards;
drop table if exists shards;

drop EXTENSION IF EXISTS "uuid-ossp";