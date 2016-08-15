# --- !Ups

create table support_user_search_permissions (
  support_user_search_permissions_id bigint signed not null auto_increment not null,
  single_query_limit            bigint signed null,
  daily_query_limit             bigint signed null,
  constraint pk_support_user_search_permissions primary key (support_user_search_permissions_id)
);

create table support_user_search_history (
  support_user_search_history_id bigint signed not null auto_increment not null,
  search_datetime               timestamp null,
  search_query                  varchar(1000) null,
  daily_search_sum              int signed null,
  developer_id                  bigint signed not null,
  constraint pk_support_user_search_history primary key (support_user_search_history_id)
);


alter table developer add column support_user_search_permissions bigint signed null;

alter table developer add constraint fk_developer_support_user_search_permissions foreign key (support_user_search_permissions) references support_user_search_permissions (support_user_search_permissions_id) on delete restrict on update restrict;
create index ix_developer_support_user_search_permissions on developer (support_user_search_permissions);

alter table support_user_search_history add constraint fk_support_user_search_history_developer_id foreign key (developer_id) references developer (developerid) on delete restrict on update restrict;
create index ix_support_user_search_history_developer_id on support_user_search_history (developer_id);


# --- !Downs

alter table developer drop foreign key fk_developer_support_user_search_permissions;
drop index ix_developer_support_user_search_permissions on developer;

alter table developer drop column support_user_search_permissions;

drop table if exists support_user_search_permissions;

alter table support_user_search_history drop foreign key fk_support_user_search_history_developer_id;
drop index ix_support_user_search_history_developer_id on support_user_search_history;

drop table if exists support_user_search_history;
