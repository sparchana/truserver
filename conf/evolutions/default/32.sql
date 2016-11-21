# --- !Ups

create table reject_reason (
  reasonid                      int unsigned auto_increment not null,
  reasonname                    text null,
  constraint pk_reject_reason primary key (reasonid)
);

alter table jobapplication drop foreign key fk_jobapplication_interview_status_id;
drop index ix_jobapplication_interview_status_id on jobapplication;

alter table jobapplication drop column interview_status_id;
alter table jobapplication drop column interviewstatuscomments;

drop table if exists interview_status;

create table interview_scheduled_status_update (
  interview_scheduled_status_update_id bigint unsigned auto_increment not null,
  interview_scheduled_status_update_uuid varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  update_timestamp              timestamp null,
  job_post_workflow_id          bigint unsigned,
  reasonid                      int unsigned,
  status_id                     int unsigned,
  interview_confirmed_status_update_note text null,
  constraint pk_interview_scheduled_status_update primary key (interview_scheduled_status_update_id)
);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_job_post_workflow_id on interview_scheduled_status_update (job_post_workflow_id);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_reasonid foreign key (reasonid) references reject_reason (reasonid) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_reasonid on interview_scheduled_status_update (reasonid);

alter table interview_scheduled_status_update add constraint fk_interview_scheduled_status_update_status_id foreign key (status_id) references job_post_workflow_status (status_id) on delete restrict on update restrict;
create index ix_interview_scheduled_status_update_status_id on interview_scheduled_status_update (status_id);

# --- !Downs

create table interview_status (
  interview_status_id           bigint signed auto_increment not null,
  interview_status_name         varchar(100) not null,
  constraint pk_interview_status primary key (interview_status_id)
);

alter table jobapplication add column interview_status_id bigint signed;
alter table jobapplication add column interviewstatuscomments varchar(5000) null;

alter table jobapplication add constraint fk_jobapplication_interview_status_id foreign key (interview_status_id) references interview_status (interview_status_id) on delete restrict on update restrict;
create index ix_jobapplication_interview_status_id on jobapplication (interview_status_id);

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_job_post_workflow_id;
drop index ix_interview_scheduled_status_update_job_post_workflow_id on interview_scheduled_status_update;

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_reasonid;
drop index ix_interview_scheduled_status_update_reasonid on interview_scheduled_status_update;

alter table interview_scheduled_status_update drop foreign key fk_interview_scheduled_status_update_status_id;
drop index ix_interview_scheduled_status_update_status_id on interview_scheduled_status_update;

drop table if exists reject_reason;
drop table if exists interview_scheduled_status_update;