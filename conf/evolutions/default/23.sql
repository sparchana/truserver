# --- !Ups
create table asset (
  asset_id                      int unsigned auto_increment not null,
  asset_title                   varchar(255) null,
  is_common                     tinyint(1) not null default 0,
  constraint pk_asset primary key (asset_id)
);

create table job_role_to_asset (
  job_role_to_asset_id          int unsigned auto_increment not null,
  job_role_to_asset_uuid        varchar(255) not null,
  job_role_id                   bigint signed,
  asset_id                      int unsigned,
  creation_timestamp            timestamp default current_timestamp not null,
  constraint pk_job_role_to_asset primary key (job_role_to_asset_id)
);

create table job_role_to_document (
  job_role_to_document_id       bigint unsigned auto_increment not null,
  job_role_to_document_uuid     varchar(255) not null,
  job_role_id                   bigint signed,
  idproofid                     int signed,
  creation_timestamp            timestamp default current_timestamp not null,
  constraint pk_job_role_to_document primary key (job_role_to_document_id)
);

create table profile_requirement (
  profile_requirement_id        bigint unsigned auto_increment not null,
  profile_requirement_uuid      varchar(255) not null,
  creation_timestamp            timestamp default current_timestamp not null,
  profile_requirement_title     varchar(255) null,
  constraint pk_profile_requirement primary key (profile_requirement_id)
);

create table pre_screen_requirement (
  pre_screen_requirement_id     bigint unsigned auto_increment not null,
  pre_screen_requirement_uuid   varchar(255) not null,
  creation_timestamp            timestamp default current_timestamp not null,
  job_post_id                   bigint signed,
  category_id                   int not null,
  id_proof_id                   int signed,
  asset_id                      int unsigned,
  profile_requirement_id      bigint unsigned,
  language_id                   int signed,
  constraint pk_pre_screen_requirement primary key (pre_screen_requirement_id)
);

create table job_post_asset_requirement (
  asset_requirement_id          bigint unsigned auto_increment not null,
  job_post_id                   bigint signed,
  asset_id                      int unsigned,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  constraint pk_job_post_asset_requirement primary key (asset_requirement_id)
);

create table job_post_document_requirement (
  job_post_document_id          bigint unsigned auto_increment not null,
  job_post_id                   bigint signed,
  id_proof_id                   int signed,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  constraint pk_job_post_document_requirement primary key (job_post_document_id)
);


create table recruiter_credit_history (
  recruiter_credit_history_id   int signed auto_increment not null,
  recruiter_credit_history_uuid varchar(255) not null not null,
  recruiter_credits_available   int signed null,
  recruiter_credits_used        int signed null,
  create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  recruitercreditcategory       bigint signed,
  constraint pk_recruiter_credit_history primary key (recruiter_credit_history_id)
);

create table recruiter_credit_category (
  recruiter_credit_category_id  bigint signed auto_increment not null,
  recruiter_credit_type         varchar(50) not null,
  recruiter_credit_unit_price   int signed null,
  constraint pk_recruiter_credit_category primary key (recruiter_credit_category_id)
);

create table recruiter_payment (
  recruiter_payment_id          bigint signed auto_increment not null,
  recruiter_payment_uuid        varchar(255) not null not null,
  recruiter_payment_amount      bigint unsigned not null,
  recruiter_payment_credit_unit_price bigint unsigned null,
  recruiter_payment_mode        int signed null,
  create_timestamp timestamp not null default current_timestamp,
  recruiter_credit_category_id  bigint signed,
  recruiterprofileid            bigint signed,
  constraint pk_recruiter_payment primary key (recruiter_payment_id)
);

create table recruiter_to_candidate_unlocked (
  recruiter_to_candidate_unlocked_id int signed auto_increment not null,
  recruiter_to_candidate_unlocked_uuid        varchar(255) not null not null,
  create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  candidateid                   bigint signed,
  constraint pk_recruiter_to_candidate_unlocked primary key (recruiter_to_candidate_unlocked_id)
);

alter table recruiter_to_candidate_unlocked add constraint fk_recruiter_to_candidate_unlocked_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_to_candidate_unlocked_recruiterprofileid on recruiter_to_candidate_unlocked (recruiterprofileid);

alter table recruiter_to_candidate_unlocked add constraint fk_recruiter_to_candidate_unlocked_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_recruiter_to_candidate_unlocked_candidateid on recruiter_to_candidate_unlocked (candidateid);

alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile add column recruiterlinkedinprofile text null;

alter table recruiter_payment add constraint fk_recruiter_payment_recruiter_credit_category_id foreign key (recruiter_credit_category_id) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_recruiter_payment_recruiter_credit_category_id on recruiter_payment (recruiter_credit_category_id);

alter table recruiter_payment add constraint fk_recruiter_payment_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_payment_recruiterprofileid on recruiter_payment (recruiterprofileid);

alter table recruiter_credit_history add constraint fk_recruiter_credit_history_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_credit_history_recruiterprofileid on recruiter_credit_history (recruiterprofileid);

alter table recruiter_credit_history add constraint fk_recruiter_credit_history_recruitercreditcategory foreign key (recruitercreditcategory) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_recruiter_credit_history_recruitercreditcategory on recruiter_credit_history (recruitercreditcategory);



alter table job_post_asset_requirement add constraint fk_job_post_asset_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_asset_requirement_job_post_id on job_post_asset_requirement (job_post_id);

alter table job_post_asset_requirement add constraint fk_job_post_asset_requirement_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_job_post_asset_requirement_asset_id on job_post_asset_requirement (asset_id);


alter table job_post_document_requirement add constraint fk_job_post_document_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_document_requirement_job_post_id on job_post_document_requirement (job_post_id);

alter table job_post_document_requirement add constraint fk_job_post_document_requirement_id_proof_id foreign key (id_proof_id) references idproof (idproofid) on delete restrict on update restrict;
create index ix_job_post_document_requirement_id_proof_id on job_post_document_requirement (id_proof_id);



alter table job_role_to_asset add constraint fk_job_role_to_asset_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_job_role_to_asset_job_role_id on job_role_to_asset (job_role_id);

alter table job_role_to_asset add constraint fk_job_role_to_asset_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_job_role_to_asset_asset_id on job_role_to_asset (asset_id);



alter table job_role_to_document add constraint fk_job_role_to_document_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_job_role_to_document_job_role_id on job_role_to_document (job_role_id);

alter table job_role_to_document add constraint fk_job_role_to_document_idproofid foreign key (idproofid) references idproof (idproofid) on delete restrict on update restrict;
create index ix_job_role_to_document_idproofid on job_role_to_document (idproofid);


alter table pre_screen_requirement add constraint fk_pre_screen_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_job_post_id on pre_screen_requirement (job_post_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_id_proof_id foreign key (id_proof_id) references idproof (idproofid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_id_proof_id on pre_screen_requirement (id_proof_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_pre_screen_requirement_asset_id on pre_screen_requirement (asset_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_profile_requirement_id foreign key (profile_requirement_id) references profile_requirement (profile_requirement_id) on delete restrict on update restrict;
create index ix_pre_screen_requirement_profile_requirement_id on pre_screen_requirement (profile_requirement_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_language_id foreign key (language_id) references language (languageid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_language_id on pre_screen_requirement (language_id);

alter table idproof add column is_common tinyint(1) not null default 0;

-- modified table, existing value to be migrated
ALTER TABLE company MODIFY COLUMN companyname text not null;
ALTER TABLE company MODIFY COLUMN companywebsite text null;
ALTER TABLE company MODIFY COLUMN companydescription text null;
ALTER TABLE company MODIFY COLUMN companyaddress text null;
ALTER TABLE company MODIFY COLUMN companylogo text null;

ALTER TABLE recruiterprofile MODIFY COLUMN recruiterdesignation text null;
ALTER TABLE recruiterprofile MODIFY COLUMN recruiterofficeaddress text null;



# --- !Downs

alter table job_post_asset_requirement drop foreign key fk_job_post_asset_requirement_job_post_id;
drop index ix_job_post_asset_requirement_job_post_id on job_post_asset_requirement;

alter table job_post_asset_requirement drop foreign key fk_job_post_asset_requirement_asset_id;
drop index ix_job_post_asset_requirement_asset_id on job_post_asset_requirement;

alter table job_post_document_requirement drop foreign key fk_job_post_document_requirement_job_post_id;
drop index ix_job_post_document_requirement_job_post_id on job_post_document_requirement;

alter table job_post_document_requirement drop foreign key fk_job_post_document_requirement_id_proof_id;
drop index ix_job_post_document_requirement_id_proof_id on job_post_document_requirement;


alter table job_role_to_asset drop foreign key fk_job_role_to_asset_job_role_id;
drop index ix_job_role_to_asset_job_role_id on job_role_to_asset;

alter table job_role_to_asset drop foreign key fk_job_role_to_asset_asset_id;
drop index ix_job_role_to_asset_asset_id on job_role_to_asset;



alter table job_role_to_document drop foreign key fk_job_role_to_document_job_role_id;
drop index ix_job_role_to_document_job_role_id on job_role_to_document;

alter table job_role_to_document drop foreign key fk_job_role_to_document_idproofid;
drop index ix_job_role_to_document_idproofid on job_role_to_document;


alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_job_post_id;
drop index ix_pre_screen_requirement_job_post_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_id_proof_id;
drop index ix_pre_screen_requirement_id_proof_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_asset_id;
drop index ix_pre_screen_requirement_asset_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_profile_requirement_id;
drop index ix_pre_screen_requirement_profile_requirement_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_language_id;
drop index ix_pre_screen_requirement_language_id on pre_screen_requirement;

alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile add column recruiterlinkedinprofile bigint signed null;

alter table recruiter_to_candidate_unlocked drop foreign key fk_recruiter_to_candidate_unlocked_recruiterprofileid;
drop index ix_recruiter_to_candidate_unlocked_recruiterprofileid on recruiter_to_candidate_unlocked;

alter table recruiter_to_candidate_unlocked drop foreign key fk_recruiter_to_candidate_unlocked_candidateid;
drop index ix_recruiter_to_candidate_unlocked_candidateid on recruiter_to_candidate_unlocked;

alter table recruiter_credit_history drop foreign key fk_recruiter_credit_history_recruiterprofileid;
drop index ix_recruiter_credit_history_recruiterprofileid on recruiter_credit_history;

alter table recruiter_credit_history drop foreign key fk_recruiter_credit_history_recruitercreditcategory;
drop index ix_recruiter_credit_history_recruitercreditcategory on recruiter_credit_history;

alter table recruiter_payment drop foreign key fk_recruiter_payment_recruiter_credit_category_id;
drop index ix_recruiter_payment_recruiter_credit_category_id on recruiter_payment;

alter table recruiter_payment drop foreign key fk_recruiter_payment_recruiterprofileid;
drop index ix_recruiter_payment_recruiterprofileid on recruiter_payment;

drop table if exists recruiter_credit_category;
drop table if exists recruiter_credit_history;
drop table if exists recruiter_payment;
drop table if exists recruiter_to_candidate_unlocked;

alter table idproof drop column is_common;

drop table if exists job_post_document_requirement;

drop table if exists job_post_asset_requirement;

drop table if exists job_role_to_asset;

drop table if exists job_role_to_document;

drop table if exists pre_screen_requirement;

drop table if exists profile_requirement;

drop table if exists asset;

ALTER TABLE company MODIFY COLUMN companyname varchar(50) not null;
ALTER TABLE company MODIFY COLUMN companywebsite varchar(30) null;
ALTER TABLE company MODIFY COLUMN companydescription varchar(5000) null;
ALTER TABLE company MODIFY COLUMN companyaddress varchar(1000) null;
ALTER TABLE company MODIFY COLUMN companylogo varchar(80) null;

ALTER TABLE recruiterprofile MODIFY COLUMN recruiterdesignation VARCHAR (50) null;
ALTER TABLE recruiterprofile MODIFY COLUMN recruiterofficeaddress VARCHAR(500) null;