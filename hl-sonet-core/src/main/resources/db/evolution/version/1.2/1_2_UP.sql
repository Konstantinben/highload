CREATE EXTENSION IF NOT EXISTS "pg_trgm";

create index if not exists idx_users_first_last_name on users USING GIN (first_name gin_trgm_ops, last_name gin_trgm_ops);

