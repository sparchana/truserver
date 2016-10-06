# --- !Ups

create table recruiter_profile_status (
	profile_status_id             int signed auto_increment not null,
	profile_status_name           varchar(255) null,
	constraint pk_recruiter_profile_status primary key (profile_status_id)
);

alter table recruiterprofile add column recruiteralternatemobile varchar(13) null;
alter table recruiterprofile add column recruiterdesignation varchar(50) null;
alter table recruiterprofile add column recruiterlinkedinprofile bigint signed null;
alter table recruiterprofile add column recruiterofficeaddress varchar(500) null;
alter table recruiterprofile add column recruiteremailstatus int signed not null default 0;
alter table recruiterprofile add column profile_status_id int signed;
alter table recruiterprofile add column recruiter_lead_recruiter_lead_id bigint signed;

alter table recruiterprofile add constraint uq_recruiterprofile_recruiter_lead_recruiter_lead_id unique (recruiter_lead_recruiter_lead_id);

alter table recruiterprofile add constraint fk_recruiterprofile_profile_status_id foreign key (profile_status_id) references recruiter_profile_status (profile_status_id) on delete restrict on update restrict;
create index ix_recruiterprofile_profile_status_id on recruiterprofile (profile_status_id);

create table recruiter_auth (
  recruiter_auth_id             bigint signed auto_increment not null,
  recruiter_id                  bigint signed not null,
  recruiter_auth_status         int signed not null not null,
  password_md5                  char(60) not null,
  password_salt                 bigint signed not null,
  auth_session_id               varchar(50) not null,
  auth_session_id_expiry_millis bigint signed not null,
  auth_create_timestamp         timestamp not null default current_timestamp,
  auth_update_timestamp         datetime(6) not null,
  constraint pk_recruiter_auth primary key (recruiter_auth_id)
);

create table recruiter_lead (
  recruiter_lead_id             bigint signed auto_increment not null,
  recruiter_lead_uuid           varchar(255) not null,
  recruiter_lead_status         int signed not null,
  recruiter_lead_name           varchar(50) not null,
  recruiter_lead_mobile         varchar(13) not null,
  recruiter_lead_channel        int signed not null,
  recruiter_lead_creation_timestamp timestamp not null,
  recruiter_lead_requirement    varchar(50) not null,
  recruiter_lead_update_timestamp datetime(6) not null,
  constraint uq_recruiter_lead_recruiter_lead_uuid unique (recruiter_lead_uuid),
  constraint pk_recruiter_lead primary key (recruiter_lead_id)
);

create table recruiterleadtolocality (
  recruiter_lead_to_locality_id bigint signed auto_increment not null,
  recruiter_lead_to_locality_create_timestamp timestamp not null default current_timestamp,
  localityid                    bigint signed,
  recruiter_lead_id             bigint signed,
  recruiter_lead_to_locality_update_timestamp datetime(6) not null,
  constraint pk_recruiterleadtolocality primary key (recruiter_lead_to_locality_id)
);

create table recruiterleadtojobrole (
  recruiter_lead_to_job_role_id bigint signed auto_increment not null,
  recruiter_lead_to_job_role_create_timestamp timestamp not null default current_timestamp,
  jobroleid                     bigint signed,
  recruiter_lead_id             bigint signed,
  recruiter_lead_to_job_role_update_timestamp datetime(6) not null,
  constraint pk_recruiterleadtojobrole primary key (recruiter_lead_to_job_role_id)
);

alter table recruiterleadtojobrole add constraint fk_recruiterleadtojobrole_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_recruiterleadtojobrole_jobroleid on recruiterleadtojobrole (jobroleid);

alter table recruiterleadtojobrole add constraint fk_recruiterleadtojobrole_recruiter_lead_id foreign key (recruiter_lead_id) references recruiter_lead (recruiter_lead_id) on delete restrict on update restrict;
create index ix_recruiterleadtojobrole_recruiter_lead_id on recruiterleadtojobrole (recruiter_lead_id);

alter table recruiterprofile add constraint fk_recruiterprofile_recruiter_lead_recruiter_lead_id foreign key (recruiter_lead_recruiter_lead_id) references recruiter_lead (recruiter_lead_id) on delete restrict on update restrict;

alter table recruiterleadtolocality add constraint fk_recruiterleadtolocality_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_recruiterleadtolocality_localityid on recruiterleadtolocality (localityid);

alter table recruiterleadtolocality add constraint fk_recruiterleadtolocality_recruiter_lead_id foreign key (recruiter_lead_id) references recruiter_lead (recruiter_lead_id) on delete restrict on update restrict;
create index ix_recruiterleadtolocality_recruiter_lead_id on recruiterleadtolocality (recruiter_lead_id);

# --- !Downs

alter table recruiterleadtojobrole drop foreign key fk_recruiterleadtojobrole_jobroleid;
drop index ix_recruiterleadtojobrole_jobroleid on recruiterleadtojobrole;

alter table recruiterleadtojobrole drop foreign key fk_recruiterleadtojobrole_recruiter_lead_id;
drop index ix_recruiterleadtojobrole_recruiter_lead_id on recruiterleadtojobrole;

alter table recruiterprofile drop foreign key fk_recruiterprofile_profile_status_id;
drop index ix_recruiterprofile_profile_status_id on recruiterprofile;

alter table recruiterprofile drop foreign key fk_recruiterprofile_recruiter_lead_recruiter_lead_id;
drop index uq_recruiterprofile_recruiter_lead_recruiter_lead_id on recruiterprofile;

alter table recruiterleadtolocality drop foreign key fk_recruiterleadtolocality_localityid;
drop index ix_recruiterleadtolocality_localityid on recruiterleadtolocality;

alter table recruiterleadtolocality drop foreign key fk_recruiterleadtolocality_recruiter_lead_id;
drop index ix_recruiterleadtolocality_recruiter_lead_id on recruiterleadtolocality;

alter table recruiterprofile drop column recruiteralternatemobile;
alter table recruiterprofile drop column recruiterdesignation;
alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile drop column recruiterofficeaddress;
alter table recruiterprofile drop column recruiteremailstatus;
alter table recruiterprofile drop column profile_status_id;
alter table recruiterprofile drop column recruiter_lead_recruiter_lead_id;

drop table if exists recruiter_profile_status;
drop table if exists recruiter_auth;
drop table if exists recruiter_lead;
drop table if exists recruiterleadtolocality;
drop table if exists recruiterleadtojobrole;