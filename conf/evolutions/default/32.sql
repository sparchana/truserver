# --- !Ups

create table interview_feedback_update (
  interview_feedback_update_id  bigint unsigned auto_increment not null,
  interview_feedback_update_uuid varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  job_post_workflow_id          bigint unsigned,
  candidateid                   bigint signed,
  jobpostid                     bigint signed,
  status_id                     int unsigned,
  interview_feedback_update_note text null,
  constraint pk_interview_feedback_update primary key (interview_feedback_update_id)
);

alter table interview_feedback_update add constraint fk_interview_feedback_update_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_interview_feedback_update_job_post_workflow_id on interview_feedback_update (job_post_workflow_id);

alter table interview_feedback_update add constraint fk_interview_feedback_update_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_interview_feedback_update_candidateid on interview_feedback_update (candidateid);

alter table interview_feedback_update add constraint fk_interview_feedback_update_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_interview_feedback_update_jobpostid on interview_feedback_update (jobpostid);

alter table interview_feedback_update add constraint fk_interview_feedback_update_status_id foreign key (status_id) references job_post_workflow_status (status_id) on delete restrict on update restrict;
create index ix_interview_feedback_update_status_id on interview_feedback_update (status_id);

# --- !Downs

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_job_post_workflow_id;
drop index ix_interview_feedback_update_job_post_workflow_id on interview_feedback_update;

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_candidateid;
drop index ix_interview_feedback_update_candidateid on interview_feedback_update;

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_jobpostid;
drop index ix_interview_feedback_update_jobpostid on interview_feedback_update;

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_status_id;
drop index ix_interview_feedback_update_status_id on interview_feedback_update;

drop table if exists interview_feedback_update;