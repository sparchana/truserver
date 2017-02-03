# --- !Ups

alter table interview_feedback_update add column reason_id int unsigned;

alter table interview_feedback_update add constraint fk_interview_feedback_update_reason_id foreign key (reason_id) references reject_reason (reason_id) on delete restrict on update restrict;
create index ix_interview_feedback_update_reason_id on interview_feedback_update (reason_id);

create table ongrid_professions (
  profession_id                 bigint signed auto_increment not null,
  profession_name               varchar(255) null,
  job_role_id                   bigint signed,
  constraint pk_ongrid_professions primary key (profession_id)
);

create table ongrid_verification_fields (
  field_id                      bigint signed auto_increment not null,
  field_name                    varchar(255) null,
  field_type                    varchar(255) null,
  constraint pk_ongrid_verification_fields primary key (field_id)
);

create table ongrid_verification_results (
  result_id                     bigint signed auto_increment not null,
  result_uuid                   varchar(255) not null not null,
  ongrid_id                     int signed null,
  ongrid_community_id           int signed null,
  candidate_id                  bigint signed,
  ongrid_field                  bigint signed,
  ongrid_verification_status    bigint signed,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              datetime(6) not null,
  constraint uq_ongrid_verification_results_result_uuid unique (result_uuid),
  constraint pk_ongrid_verification_results primary key (result_id)
);

create table ongrid_verification_status (
  status_id                     bigint signed auto_increment not null,
  status_name                   varchar(255) null,
  constraint pk_ongrid_verification_status primary key (status_id)
);

alter table idproofreference add column verification_status bigint signed;

alter table ongrid_professions add constraint fk_ongrid_professions_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_ongrid_profession_job_role_id on ongrid_professions (job_role_id);

alter table ongrid_verification_results add constraint fk_ongrid_verification_results_candidate_id foreign key (candidate_id) references candidate (candidateid) on delete restrict on update restrict;
create index ix_ongrid_verification_results_candidate_id on ongrid_verification_results (candidate_id);

alter table ongrid_verification_results add constraint fk_ongrid_verification_results_ongrid_field foreign key (ongrid_field) references ongrid_verification_fields (field_id) on delete restrict on update restrict;
create index ix_ongrid_verification_results_ongrid_field on ongrid_verification_results (ongrid_field);

alter table ongrid_verification_results add constraint fk_ongrid_verification_results_ongrid_verification_status foreign key (ongrid_verification_status) references ongrid_verification_status (status_id) on delete restrict on update restrict;
create index ix_ongrid_verification_results_ongrid_verification_status on ongrid_verification_results (ongrid_verification_status);

alter table idproofreference add constraint fk_idproofreference_verification_status foreign key (verification_status) references ongrid_verification_status (status_id) on delete restrict on update restrict;
create index ix_idproofreference_verification_status on idproofreference (verification_status);

create table ongrid_request_stats (
  request_id                    bigint signed auto_increment not null,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              datetime(6) not null,
  verification_type             varchar(500) not null,
  request_url                   text null,
  request_text                  text null,
  response_text                 text null,
  response_status               text null,
  constraint pk_ongrid_request_stats primary key (request_id)
);

# --- !Downs

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_reason_id;
drop index ix_interview_feedback_update_reason_id on interview_feedback_update;

alter table interview_feedback_update drop column reason_id;

alter table ongrid_professions drop foreign key fk_ongrid_professions_job_role_id;
drop index ix_ongrid_profession_job_role_id on ongrid_professions;

alter table ongrid_professions drop foreign key fk_ongrid_professions_job_role_id;

alter table ongrid_verification_results drop foreign key fk_ongrid_verification_results_candidate_id;
drop index ix_ongrid_verification_results_candidate_id on ongrid_verification_results;

alter table ongrid_verification_results drop foreign key fk_ongrid_verification_results_ongrid_field;
drop index ix_ongrid_verification_results_ongrid_field on ongrid_verification_results;

alter table ongrid_verification_results drop foreign key fk_ongrid_verification_results_ongrid_verification_status;
drop index ix_ongrid_verification_results_ongrid_verification_status on ongrid_verification_results;

alter table idproofreference drop foreign key fk_idproofreference_verification_status;
drop index ix_idproofreference_verification_status on idproofreference;

alter table idproofreference drop column verification_status;

drop table if exists ongrid_professions;

drop table if exists ongrid_verification_fields;

drop table if exists ongrid_verification_results;

drop table if exists ongrid_verification_status;

drop table if exists ongrid_request_stats;
