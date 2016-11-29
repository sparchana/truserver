# --- !Ups

alter table interview_feedback_update add column reason_id int unsigned;

alter table interview_feedback_update add constraint fk_interview_feedback_update_reason_id foreign key (reason_id) references reject_reason (reason_id) on delete restrict on update restrict;
create index ix_interview_feedback_update_reason_id on interview_feedback_update (reason_id);

# --- !Downs

alter table interview_feedback_update drop foreign key fk_interview_feedback_update_reason_id;
drop index ix_interview_feedback_update_reason_id on interview_feedback_update;

alter table interview_feedback_update drop column reason_id;