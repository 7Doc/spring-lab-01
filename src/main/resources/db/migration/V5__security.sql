create table app_user (
                          id            bigserial primary key,
                          username      varchar(60)  not null unique,
                          password_hash varchar(100) not null,     -- BCrypt is 60 characters
                          enabled       boolean      not null default true
);

create table user_role (
                           user_id bigint      not null references app_user (id),
                           role    varchar(20) not null,
                           primary key (user_id, role)
);

-- two users; the hashes are of user123 and admin123
insert into app_user (username, password_hash) values
                                                   ('user',  '$2a$10$Bex4wC/lmZmX4ad9.PU05exqPfy4b56QDYZeMPSOdMb87ArvOMmIu'),   -- paste YOUR hash, generated in 2.2
                                                   ('admin', '$2a$10$4dKEnq8vQqQEFsIwQWxU3uY5FwEuFhd35o46yL.PFK2L7lC8zGd2G');

insert into user_role (user_id, role)
select id, 'USER'  from app_user;
insert into user_role (user_id, role)
select id, 'ADMIN' from app_user where username = 'admin';
