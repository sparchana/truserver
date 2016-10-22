# --- !Ups

create table job_post_workflow (
  job_post_workflow_id          bigint unsigned auto_increment not null,
  job_post_workflow_uuid        varchar(255) not null not null,
  job_post_id                   bigint signed,
  candidate_id                  bigint signed,
  creation_timestamp            timestamp default current_timestamp not null not null,
  status_id                     int unsigned,
  createdby                     varchar(255) null not null,
  constraint pk_job_post_workflow primary key (job_post_workflow_id)
);

create table job_post_workflow_status (
  status_id                     int unsigned auto_increment not null,
  status_title                  varchar(255) null,
  constraint pk_job_post_workflow_status primary key (status_id)
);

alter table job_post_workflow add constraint fk_job_post_workflow_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_workflow_job_post_id on job_post_workflow (job_post_id);

alter table job_post_workflow add constraint fk_job_post_workflow_candidate_id foreign key (candidate_id) references candidate (candidateid) on delete restrict on update restrict;
create index ix_job_post_workflow_candidate_id on job_post_workflow (candidate_id);

alter table job_post_workflow add constraint fk_job_post_workflow_status_id foreign key (status_id) references job_post_workflow_status (status_id) on delete restrict on update restrict;
create index ix_job_post_workflow_status_id on job_post_workflow (status_id);

# --- !Downs
alter table job_post_workflow drop foreign key fk_job_post_workflow_job_post_id;
drop index ix_job_post_workflow_job_post_id on job_post_workflow;

alter table job_post_workflow drop foreign key fk_job_post_workflow_candidate_id;
drop index ix_job_post_workflow_candidate_id on job_post_workflow;

alter table job_post_workflow drop foreign key fk_job_post_workflow_status_id;
drop index ix_job_post_workflow_status_id on job_post_workflow;


drop table if exists job_post_workflow;

drop table if exists job_post_workflow_status;