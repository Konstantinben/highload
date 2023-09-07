insert into friends (user_id, friend_id)
select 1, fr.id from users fr
where fr.uuid ='237796c0-008e-4c98-bd49-19d14f8d6785';

insert into friends (user_id, friend_id)
select 1, fr.id from users fr
where fr.uuid ='8cba6294-7999-4fad-a785-f071e0833da8';

insert into friends (user_id, friend_id)
select 1, fr.id from users fr
where fr.uuid ='f217432f-c3b4-459f-8e6c-1a291d13f7e0';



delete from friends where user_id = 1
and friend_id = (select fr.id from users fr
where fr.uuid ='237796c0-008e-4c98-bd49-19d14f8d6785');


select us.* from friends fr
join users us on us.id = fr.friend_id
where fr.user_id=1;

select shard_id from shards;

insert into dialog_shards (key_hash, shard_id)
values (-1210090318, 1)
returning key_hash, shard_id, resharding, created_date;

insert into dialog_messages (uuid, key_hash, shard_id, from_user_id, to_user_id, message)
values (-1210090318, 1)




select * from tesst;

--------- server 1 -------------
create table news (
  id bigint not null,
  shard_id int not null,
  CONSTRAINT shard_id_check CHECK (shard_id=1),
  message text not null
);

--------- server 2 -------------
create table news (
  id bigint not null,
  shard_id int not null,
  CONSTRAINT shard_id_check CHECK (shard_id=2),
  message text not null
);

--------------------------------

create EXTENSION postgres_fdw;

drop server messages_1_server CASCADE;
drop server messages_2_server CASCADE ;

drop rule dialog_messages_insert_to_1 on dialog_messages;
drop rule dialog_messages_insert_to_2 on dialog_messages;

drop rule dialog_messages_insert_to_1 on dialog_messages;
drop rule dialog_messages_insert_to_2 on dialog_messages;

drop rule dialog_messages_insert on dialog_messages;
drop rule dialog_messages_update on dialog_messages;
drop rule dialog_messages_delete on dialog_messages;

--------- server 1 -------------
create server messages_1_server
FOREIGN DATA WRAPPER  postgres_fdw
OPTIONS (host 'pgshard1', port '5432', dbname 'postgres');

create user mapping for postgres
server messages_1_server
options (user 'postgres', password 'pass');

create foreign table news_1 (
                      id bigint not null,
                      shard_id int not null,
                      message text not null
)
server messages_1_server
options (schema_name 'public', table_name 'news');

--------- server 2 -------------
create server messages_2_server
    FOREIGN DATA WRAPPER  postgres_fdw
    OPTIONS (host 'pgshard2', port '5432', dbname 'postgres');

create user mapping for postgres
    server messages_2_server
    options (user 'postgres', password 'pass');

create foreign table news_2 (
    id bigint not null,
    shard_id int not null,
    message text not null
    )
    server messages_2_server
    options (schema_name 'public', table_name 'news');

--------------------------------
create view news AS
    select * from news_1
        union all
    select * from news_2;

--------------------------------
create rule news_insert as on insert to news do instead nothing ;
create rule news_update as on update to news do instead nothing ;
create rule news_delete as on delete to news do instead nothing ;

create rule news_insert_to_1 as on insert to news
    where (shard_id = 1)
do instead insert into news_1 values (NEW.*);

create rule news_insert_to_2 as on insert to news
    where (shard_id = 2)
    do instead insert into news_2 values (NEW.*);
--------------------------------

insert into news(id, shard_id, message)
values
(1, 1, 'Message 1'),
(2, 2, 'Message 2');

select * from news;


SELECT n.nspname as "Schema",
       c.relname as "Name",
       CASE c.relkind WHEN 'r' THEN 'table' WHEN 'v' THEN 'view' WHEN 'm' THEN 'materialized view' WHEN 'i' THEN 'index' WHEN 'S' THEN 'sequence' WHEN 's' THEN 'special' WHEN 'f' THEN 'foreign table' END as "Type",
       pg_catalog.pg_get_userbyid(c.relowner) as "Owner"
FROM pg_catalog.pg_class c
         LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind IN ('r','v','m','S','f','')
  AND n.nspname <> 'pg_catalog'
  AND n.nspname <> 'information_schema'
  AND n.nspname !~ '^pg_toast'
  AND pg_catalog.pg_table_is_visible(c.oid)
ORDER BY 1,2;

SELECT
    c.relname as "Name",
    CASE c.relkind WHEN 'r' THEN 'table'
                   WHEN 'v' THEN 'view'
                   WHEN 'm' THEN 'materialized view'
                   WHEN 'i' THEN 'index'
                   WHEN 'S' THEN 'sequence'
                   WHEN 's' THEN 'special'
                   WHEN 'f' THEN 'foreign table' END as "Type"
FROM pg_catalog.pg_class c
         LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind IN ('r','v','m','S','f','')
  AND n.nspname <> 'pg_catalog'
  AND n.nspname <> 'information_schema'
  AND n.nspname !~ '^pg_toast'
  AND pg_catalog.pg_table_is_visible(c.oid);


SELECT
    p.proname as "Name",
    'function'
FROM pg_catalog.pg_proc p
         LEFT JOIN pg_catalog.pg_namespace n ON n.oid = p.pronamespace
WHERE pg_catalog.pg_function_is_visible(p.oid)
  AND n.nspname <> 'pg_catalog'
  AND n.nspname <> 'information_schema';


select relations.table_name as table_name,
       count(relations.table_name) as relationships,
       count(relations.referenced_tables) as foreign_keys,
       count(relations.referencing_tables) as references,
       count(distinct related_table) as related_tables,
       count(distinct relations.referenced_tables) as referenced_tables,
       count(distinct relations.referencing_tables) as referencing_tables
from(
        select pk_tco.table_schema || '.' || pk_tco.table_name as table_name,
               fk_tco.table_schema || '.' || fk_tco.table_name as related_table,
               fk_tco.table_name as referencing_tables,
               null::varchar(100) as referenced_tables
        from information_schema.referential_constraints rco
                 join information_schema.table_constraints fk_tco
                      on rco.constraint_name = fk_tco.constraint_name
                          and rco.constraint_schema = fk_tco.table_schema
                 join information_schema.table_constraints pk_tco
                      on rco.unique_constraint_name = pk_tco.constraint_name
                          and rco.unique_constraint_schema = pk_tco.table_schema
        union all
        select fk_tco.table_schema || '.' || fk_tco.table_name as table_name,
               pk_tco.table_schema || '.' || pk_tco.table_name as related_table,
               null as referencing_tables,
               pk_tco.table_name as referenced_tables
        from information_schema.referential_constraints rco
                 join information_schema.table_constraints fk_tco
                      on rco.constraint_name = fk_tco.constraint_name
                          and rco.constraint_schema = fk_tco.table_schema
                 join information_schema.table_constraints pk_tco
                      on rco.unique_constraint_name = pk_tco.constraint_name
                          and rco.unique_constraint_schema = pk_tco.table_schema
    ) relations
group by table_name
order by relationships desc;