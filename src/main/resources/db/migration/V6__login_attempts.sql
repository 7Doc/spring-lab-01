alter table app_user
    add column failed_attempts int not null default 0,
    add column lock_time timestamp null;
