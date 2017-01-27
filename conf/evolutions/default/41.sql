
# --- !Ups

create table truly (
  truly_id                      int unsigned auto_increment not null,
  longurl                       text null,
  shorturl                      varchar(255) null,
  hash                          varchar(255) null,
  create_timestamp              timestamp default current_timestamp not null,
  truly_access_level            int(1) signed not null default 0,
  hit_rate                      bigint unsigned not null default 0,
  update_timestamp              datetime(6) not null,
  constraint pk_truly primary key (truly_id)
);


# --- !Downs

drop table if exists truly;