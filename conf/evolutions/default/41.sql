# --- !Ups

create table truly (
  truly_id                      bigint unsigned auto_increment not null,
  longurl                       text null,
  shorturl                      varchar(255) null,
  hash                          varchar(255) null,
  create_timestamp              timestamp default current_timestamp not null,
  truly_access_level            int(1) signed not null default 0,
  hit_rate                      bigint unsigned not null default 0,
  update_timestamp              datetime(6) not null,
  constraint pk_truly primary key (truly_id)
);

create table sms_type (
  sms_type_id                   int signed auto_increment not null,
  type_name                     varchar(50) null,
  constraint pk_sms_type primary key (sms_type_id)
);

alter table sms_report add column smstype int signed;

alter table sms_report add constraint fk_sms_report_smstype foreign key (smstype) references sms_type (sms_type_id) on delete restrict on update restrict;
create index ix_sms_report_smstype on sms_report (smstype);

# --- !Downs

alter table sms_report drop foreign key fk_sms_report_smstype;
drop index ix_sms_report_smstype on sms_report;

alter table sms_report drop column smstype;
drop table if exists truly;
drop table if exists sms_type;