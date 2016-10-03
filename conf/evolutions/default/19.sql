# --- !Ups

alter table jobpost add column jobpostpartnerinterviewincentive bigint signed null;
alter table jobpost add column jobpostpartnerjoiningincentive bigint signed null;

alter table jobapplication add column interviewtimeslot bigint signed;
alter table jobapplication add column scheduledinterviewdate date null;

create table interview_details (
  interview_details_id          int signed auto_increment not null,
  interview_days                binary(7) null,
  jobpostid                     bigint signed,
  interview_time_slot_id        bigint signed,
  update_time_stamp             timestamp null not null,
  constraint pk_interview_details primary key (interview_details_id)
);

create table interview_time_slot (
  interview_time_slot_id        bigint signed auto_increment not null,
  interview_time_slot_name      varchar(20) null,
  constraint pk_interview_time_slot primary key (interview_time_slot_id)
);

alter table interview_details add constraint fk_interview_details_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_interview_details_jobpostid on interview_details (jobpostid);

alter table interview_details add constraint fk_interview_details_interview_time_slot_id foreign key (interview_time_slot_id) references interview_time_slot (interview_time_slot_id) on delete restrict on update restrict;
create index ix_interview_details_interview_time_slot_id on interview_details (interview_time_slot_id);


create table related_jobrole (
  id                            bigint signed auto_increment not null,
  job_role_id                   bigint signed,
  related_job_role_id           bigint signed,
  weight                        double(2, 2) null,
  constraint pk_related_jobrole primary key (id)
);

alter table related_jobrole add constraint fk_related_jobrole_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_related_jobrole_job_role_id on related_jobrole (job_role_id);

alter table related_jobrole add constraint fk_related_jobrole_related_job_role_id foreign key (related_job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_related_jobrole_related_job_role_id on related_jobrole (related_job_role_id);

alter table candidate_assessment_attempt add column ca_attempt_uuid varchar(255) not null;

-- after creating ca_attempt_uuid column in candidate_assessment_attempt, run UPDATE trujobsdev.candidate_assessment_attempt set ca_attempt_uuid = uuid()

# --- !Downs

alter table jobpost drop column jobpostpartnerinterviewincentive;
alter table jobpost drop column jobpostpartnerjoiningincentive;

alter table jobapplication drop column interviewtimeslot;
alter table jobapplication drop column scheduledinterviewdate;

alter table interview_details drop foreign key fk_interview_details_jobpostid;
drop index ix_interview_details_jobpostid on interview_details;

alter table interview_details drop foreign key fk_interview_details_interview_time_slot_id;
drop index ix_interview_details_interview_time_slot_id on interview_details;

drop table if exists interview_details;
drop table if exists interview_time_slot;

alter table related_jobrole drop foreign key fk_related_jobrole_job_role_id;
drop index ix_related_jobrole_job_role_id on related_jobrole;

alter table related_jobrole drop foreign key fk_related_jobrole_related_job_role_id;
drop index ix_related_jobrole_related_job_role_id on related_jobrole;

alter table candidate_assessment_attempt drop column ca_attempt_uuid;

drop table if exists related_jobrole;
