CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists users (
	id serial primary key,
	"email" text not null unique,
	uuid uuid not null default uuid_generate_v4(),
	first_name varchar(100) not null,
	last_name varchar(100),
	gender varchar(1),
	birthdate date,
	city varchar(255),
    biography text,
    role varchar(20),
    password varchar(255)
);

