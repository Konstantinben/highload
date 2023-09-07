alter table users add constraint users_uuid_unq unique (uuid);

create table if not exists friends (
  user_id integer references users(id) on delete cascade,
  friend_id integer references users(id) on delete cascade,
  constraint onboarded_apps_pk primary key(user_id,friend_id)
);

create table if not exists posts (
  id bigserial primary key,
  user_id integer references users(id) on delete cascade,
  uuid uuid unique not null default uuid_generate_v4(),
  user_uuid uuid not null references users(uuid),
  user_first_name text not null,
  user_last_name text,
  message text not null,
  created_date timestamp without time zone not null default (now() at time zone 'utc'),
  updated_date timestamp without time zone DEFAULT NULL
);