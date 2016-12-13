# --- !Ups
create table scheduler_stats (
  id                            bigint signed auto_increment not null,
  scheduler_type_id             bigint signed,
  scheduler_sub_type_id         bigint signed,
  note                          text null,
  completion_status             tinyint(1) not null,
  start_timestamp               timestamp null,
  end_timestamp                 timestamp null,
  constraint pk_scheduler_stats primary key (id)
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

alter table scheduler_stats add constraint fk_scheduler_stats_scheduler_type_id foreign key (scheduler_type_id) references scheduler_type (scheduler_type_id) on delete restrict on update restrict;
create index ix_scheduler_stats_scheduler_type_id on scheduler_stats (scheduler_type_id);

alter table scheduler_stats add constraint fk_scheduler_stats_scheduler_sub_type_id foreign key (scheduler_sub_type_id) references scheduler_sub_type (scheduler_sub_type_id) on delete restrict on update restrict;
create index ix_scheduler_stats_scheduler_sub_type_id on scheduler_stats (scheduler_sub_type_id);

# --- !Downs

alter table scheduler_stats drop foreign key fk_scheduler_stats_scheduler_type_id;
drop index ix_scheduler_stats_scheduler_type_id on scheduler_stats;

alter table scheduler_stats drop foreign key fk_scheduler_stats_scheduler_sub_type_id;
drop index ix_scheduler_stats_scheduler_sub_type_id on scheduler_stats;

drop table if exists scheduler_stats;

drop table if exists scheduler_sub_type;

drop table if exists scheduler_type;

