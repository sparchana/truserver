# --- !Ups

create table reject_reason (
  reasonid                      int unsigned auto_increment not null,
  reasonname                    text null,
  constraint pk_reject_reason primary key (reasonid)
);

alter table jobapplication drop foreign key fk_jobapplication_interview_status_id;
drop index ix_jobapplication_interview_status_id on jobapplication;

drop table if exists interview_status;

alter table jobapplication drop column interview_status_id;
alter table jobapplication drop column interviewstatuscomments;

# --- !Downs

drop table if exists reject_reason;

create table interview_status (
  interview_status_id           bigint signed auto_increment not null,
  interview_status_name         varchar(100) not null,
  constraint pk_interview_status primary key (interview_status_id)
);


alter table jobapplication add column interview_status_id bigint signed;
alter table jobapplication add column interviewstatuscomments varchar(5000) null;

alter table jobapplication add constraint fk_jobapplication_interview_status_id foreign key (interview_status_id) references interview_status (interview_status_id) on delete restrict on update restrict;
create index ix_jobapplication_interview_status_id on jobapplication (interview_status_id);
