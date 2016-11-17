# --- !Ups

create table interview_confirmed_status_update (
  interview_confirmed_status_update_id bigint unsigned auto_increment not null,
  interview_confirmed_status_update_uuid varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  update_timestamp              timestamp null,
  job_post_workflow_id          bigint unsigned,
  interview_confirmed_status_update_note text null,
  constraint pk_interview_confirmed_status_update primary key (interview_confirmed_status_update_id)
);

alter table interview_confirmed_status_update add constraint fk_interview_confirmed_status_update_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_interview_confirmed_status_update_job_post_workflow_id on interview_confirmed_status_update (job_post_workflow_id);

alter table interview_details add column latitude double(10,6) null;
alter table interview_details add column longitude double(10,6) null;
alter table interview_details add column placeid double(10,6) null;

# --- !Downs

alter table interview_confirmed_status_update drop foreign key fk_interview_confirmed_status_update_job_post_workflow_id;
drop index ix_interview_confirmed_status_update_job_post_workflow_id on interview_confirmed_status_update;

alter table interview_details drop column latitude;
alter table interview_details drop column longitude;
alter table interview_details drop column placeid;

drop table if exists interview_confirmed_status_update;