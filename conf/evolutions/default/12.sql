# --- !Ups
create table partner (
  partner_id                    bigint signed auto_increment not null,
  partner_uuid                  varchar(255) not null not null,
  partner_name                  varchar(50) not null,
  partner_last_name             varchar(50) null,
  partner_mobile                varchar(13) not null,
  partner_email                 varchar(255) null,
  partner_company               varchar(255) null,
  partner_locality              bigint signed,
  partner_type                  bigint signed,
  partner_create_timestamp      timestamp not null default current_timestamp,
  lead_leadid                   bigint signed,
  partner_status_id             int signed,
  partner_update_timestamp      datetime(6) not null,
  constraint uq_partner_partner_uuid unique (partner_uuid),
  constraint uq_partner_lead_leadid unique (lead_leadid),
  constraint pk_partner primary key (partner_id)
);

create table partner_auth (
  partner_auth_id               bigint signed auto_increment not null,
  partner_id                    bigint signed not null,
  partner_auth_status           int signed not null not null,
  password_md5                  char(60) not null,
  password_salt                 bigint signed not null,
  auth_session_id               varchar(50) not null,
  auth_session_id_expiry_millis bigint signed not null,
  auth_create_timestamp         timestamp not null default current_timestamp,
  auth_update_timestamp         datetime(6) not null,
  constraint pk_partner_auth primary key (partner_auth_id)
);

create table partner_profile_status (
	profile_status_id
	int signed auto_increment not null,
	profile_status_name
	varchar(255) null,
	constraint pk_partner_profile_status primary key (profile_status_id)
);

create table partner_type (
   partner_type_id               bigint signed auto_increment not null,
   partner_type_name             varchar(100) not null,
   constraint pk_partner_type primary key (partner_type_id)
);

alter table partner add constraint fk_partner_lead_leadid foreign key (lead_leadid) references lead (leadid) on delete restrict on update restrict;

alter table partner add constraint fk_partner_partner_status_id foreign key (partner_status_id) references partner_profile_status (profile_status_id) on delete restrict on update restrict;
create index ix_partner_partner_status_id on partner (partner_status_id);

alter table partner add constraint fk_partner_partner_locality foreign key (partner_locality) references locality (localityid) on delete restrict on update restrict;
create index ix_partner_partner_locality on partner (partner_locality);

alter table partner add constraint fk_partner_partner_type foreign key (partner_type) references partner_type (partner_type_id) on delete restrict on update restrict;
create index ix_partner_partner_type on partner (partner_type);

# --- !Downs

alter table partner drop foreign key fk_partner_lead_leadid;

alter table partner drop foreign key fk_partner_partner_status_id;
drop index ix_partner_partner_status_id on partner;

alter table partner drop foreign key fk_partner_partner_locality;
drop index ix_partner_partner_locality on partner;

alter table partner drop foreign key fk_partner_partner_type;
drop index ix_partner_partner_type on partner;

drop table if exists partner;

drop table if exists partner_auth;

drop table if exists partner_profile_status;

drop table if exists partner_type;
