# --- !Ups

create table job_post_workflow (
  job_post_workflow_id          bigint unsigned auto_increment not null,
  job_post_workflow_uuid        varchar(255) not null not null,
  jobpostid                     bigint signed,
  candidateid                   bigint signed,
  creationtimestamp             timestamp default current_timestamp not null not null,
  createdby                     varchar(255) null not null,
  constraint pk_job_post_workflow primary key (job_post_workflow_id)
);

create table job_post_workflow_status (
  status_id                     int unsigned auto_increment not null,
  status_title                  varchar(255) null,
  constraint pk_job_post_workflow_status primary key (status_id)
);

alter table job_post_workflow add constraint fk_job_post_workflow_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_workflow_jobpostid on job_post_workflow (jobpostid);

alter table job_post_workflow add constraint fk_job_post_workflow_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_job_post_workflow_candidateid on job_post_workflow (candidateid);

# --- !Downs

alter table job_post_workflow drop foreign key fk_job_post_workflow_jobpostid;
drop index ix_job_post_workflow_jobpostid on job_post_workflow;

alter table job_post_workflow drop foreign key fk_job_post_workflow_candidateid;
drop index ix_job_post_workflow_candidateid on job_post_workflow;


drop table if exists job_post_workflow;

drop table if exists job_post_workflow_status;