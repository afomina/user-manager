CREATE  KEYSPACE IF NOT EXISTS user_manager
   WITH REPLICATION = {'class':'SimpleStrategy','replication_factor':1};

USE user_manager;

create table if not exists user(
    id uuid primary key,
    email varchar,
    password varchar,
    first_name varchar,
    last_name varchar,
    avatar blob,
    role int
);

create table if not exists user_email(
    id uuid,
    email varchar,
    primary key (email, id)
);
