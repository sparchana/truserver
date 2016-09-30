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