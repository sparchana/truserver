# --- !Ups
create table scheduler (
  id                            bigint signed auto_increment not null,
  scheduler_type_id             bigint signed,
  scheduler_sub_type_id         bigint signed,
  recipient                     varchar(255) not null,
  message                       text null,
  completion_status             tinyint(1) not null,
  event_start_timestamp         timestamp null,
  event_end_timestamp           timestamp null,
  constraint pk_scheduler primary key (id)
);

create table scheduler_type (
  scheduler_type_id             bigint signed auto_increment not null,
  scheduler_type_title          varchar(255) not null,
  constraint pk_scheduler_type primary key (scheduler_type_id)
);

create table scheduler_sub_type (
  scheduler_sub_type_id         bigint signed auto_increment not null,
  scheduler_sub_type_title      varchar(255) not null,
  constraint pk_scheduler_sub_type primary key (scheduler_sub_type_id)
);

alter table scheduler add constraint fk_scheduler_scheduler_type_id foreign key (scheduler_type_id) references scheduler_type (scheduler_type_id) on delete restrict on update restrict;
create index ix_scheduler_scheduler_type_id on scheduler (scheduler_type_id);

alter table scheduler add constraint fk_scheduler_scheduler_sub_type_id foreign key (scheduler_sub_type_id) references scheduler_sub_type (scheduler_sub_type_id) on delete restrict on update restrict;
create index ix_scheduler_scheduler_sub_type_id on scheduler (scheduler_sub_type_id);

# --- !Downs

alter table scheduler drop foreign key fk_scheduler_scheduler_type_id;
drop index ix_scheduler_scheduler_type_id on scheduler;

alter table scheduler drop foreign key fk_scheduler_scheduler_sub_type_id;
drop index ix_scheduler_scheduler_sub_type_id on scheduler;

drop table if exists scheduler;

drop table if exists scheduler_sub_type;

drop table if exists scheduler_type;

