# --- !Ups

alter table job_post_workflow add column interview_round int unsigned null;
alter table job_post_workflow add column interview_recruiter_id  bigint signed null;


alter table job_post_workflow add constraint fk_job_post_workflow_interview_recruiter_id foreign key (interview_recruiter_id) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_job_post_workflow_interview_recruiter_id on job_post_workflow (interview_recruiter_id);

# --- !Downs

alter table job_post_workflow drop column interview_round;

alter table job_post_workflow drop foreign key fk_job_post_workflow_interview_recruiter_id;
drop index ix_job_post_workflow_interview_recruiter_id on job_post_workflow;

alter table job_post_workflow drop column interview_recruiter_id;
