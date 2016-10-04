# --- !Ups                                                                                 # --- !Ups

alter table recruiterprofile add column recruiteralternatemobile varchar(13) null;
alter table recruiterprofile add column recruiterdesignation varchar(50) null;
alter table recruiterprofile add column recruiterlinkedinprofile bigint signed null;
alter table recruiterprofile add column recruiterofficeaddress varchar(500) null;
alter table recruiterprofile add column recruiteremailstatus int signed not null default 0;

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

# --- !Downs

alter table recruiterprofile drop column recruiteralternatemobile;
alter table recruiterprofile drop column recruiterdesignation;
alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile drop column recruiterofficeaddress;
alter table recruiterprofile drop column recruiteremailstatus;

drop table if exists recruiter_auth;