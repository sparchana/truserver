# --- !Ups

alter table interview_details add column latitude double(10,6) null;
alter table interview_details add column longitude double(10,6) null;
alter table interview_details add column placeid double(10,6) null;
alter table interview_details add column reviewapplication int(1) null;

create table reject_reason (
  reason_id                     int unsigned auto_increment not null,
  reason_name                   text null,
  reason_type                   int null,
  constraint pk_reject_reason primary key (reason_id)
);

create table candidate_interview_status_update (
  candidate_interview_status_update_id bigint unsigned auto_increment not null,
  candidate_interview_status_update_uuid varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  job_post_workflow_id          bigint unsigned,
  candidateid                   bigint signed,
  jobpostid                     bigint signed,
  reason_id                     int unsigned,
  candidate_interview_status_update_note text null,
  constraint pk_candidate_interview_status_update primary key (candidate_interview_status_update_id)
);

alter table candidate_interview_status_update add constraint fk_candidate_interview_status_update_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_candidate_interview_status_update_job_post_workflow_id on candidate_interview_status_update (job_post_workflow_id);

alter table candidate_interview_status_update add constraint fk_candidate_interview_status_update_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_interview_status_update_candidateid on candidate_interview_status_update (candidateid);

alter table candidate_interview_status_update add constraint fk_candidate_interview_status_update_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_candidate_interview_status_update_jobpostid on candidate_interview_status_update (jobpostid);

alter table candidate_interview_status_update add constraint fk_candidate_interview_status_update_reason_id foreign key (reason_id) references reject_reason (reason_id) on delete restrict on update restrict;
create index ix_candidate_interview_status_update_reason_id on candidate_interview_status_update (reason_id);

create table interview_scheduled_status_update (
  interview_scheduled_status_update_id bigint unsigned auto_increment not null,
  interview_scheduled_status_update_uuid varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  update_timestamp              timestamp null,
  job_post_workflow_id          bigint unsigned,
  reason_id                     int unsigned,
  status_id                     int unsigned,
  interview_confirmed_status_update_note text null,
  constraint pk_interview_scheduled_status_update primary key (interview_scheduled_status_update_id)
);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_job_post_workflow_id on interview_scheduled_status_update (job_post_workflow_id);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_reason_id foreign key (reason_id) references reject_reason (reason_id) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_reason_id on interview_scheduled_status_update (reason_id);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_status_id foreign key (status_id) references job_post_workflow_status (status_id) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_status_id on interview_scheduled_status_update (status_id);

# --- !Downs

alter table interview_details drop column latitude;
alter table interview_details drop column longitude;
alter table interview_details drop column placeid;
alter table interview_details drop column reviewapplication;

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_job_post_workflow_id;
drop index ix_interview_scheduled_status_update_job_post_workflow_id on interview_scheduled_status_update;

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_reason_id;
drop index ix_interview_scheduled_status_update_reason_id on interview_scheduled_status_update;

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_status_id;
drop index ix_interview_scheduled_status_update_status_id on interview_scheduled_status_update;

alter table candidate_interview_status_update drop foreign key fk_candidate_interview_status_update_job_post_workflow_id;
drop index ix_candidate_interview_status_update_job_post_workflow_id on candidate_interview_status_update;

alter table candidate_interview_status_update drop foreign key fk_candidate_interview_status_update_candidateid;
drop index ix_candidate_interview_status_update_candidateid on candidate_interview_status_update;

alter table candidate_interview_status_update drop foreign key fk_candidate_interview_status_update_jobpostid;
drop index ix_candidate_interview_status_update_jobpostid on candidate_interview_status_update;

alter table candidate_interview_status_update drop foreign key fk_candidate_interview_status_update_reason_id;
drop index ix_candidate_interview_status_update_reason_id on candidate_interview_status_update;

drop table if exists interview_scheduled_status_update;
drop table if exists candidate_interview_status_update;
drop table if exists reject_reason;

