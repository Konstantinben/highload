drop table if exists posts;

drop table  if exists friends;

ALTER TABLE users DROP CONSTRAINT IF EXISTS  users_uuid_unq;