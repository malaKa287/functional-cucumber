create database TEST_DB;
create schema if not exists TEST_SCHEMA;

create table if not exists TEST_SCHEMA.TEST_USERS
(
    ID         numeric not null primary key,
    FIRST_NAME varchar not null,
    LAST_NAME  varchar
);

create table if not exists TEST_SCHEMA.TEST_CONTACTS
(
    ID           numeric not null primary key,
    USER_ID      numeric,
    PHONE_NUMBER varchar not null,
    EMAIL        varchar,

    constraint fk_users foreign key (USER_ID) references TEST_SCHEMA.TEST_USERS (ID)
);