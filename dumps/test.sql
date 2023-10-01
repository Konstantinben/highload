create extension if not exists pg_stat_statements;

create table test (
  id bigint not null,
  message text not null
);