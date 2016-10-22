# --- !Ups
create table job_post_language_requirement (
  languagerequirementid         bigint unsigned auto_increment not null,
  jobpostid                     bigint signed,
  languageid                    int signed,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  constraint pk_job_post_language_requirement primary key (languagerequirementid)
);

alter table job_post_language_requirement add constraint fk_job_post_language_requirement_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_language_requirement_jobpostid on job_post_language_requirement (jobpostid);

alter table job_post_language_requirement add constraint fk_job_post_language_requirement_languageid foreign key (languageid) references language (languageid) on delete restrict on update restrict;
create index ix_job_post_language_requirement_languageid on job_post_language_requirement (languageid);

alter table jobpost add column jobPostMaxAge int unsigned null;

# --- !Downs

alter table job_post_language_requirement drop foreign key fk_job_post_language_requirement_jobpostid;
drop index ix_job_post_language_requirement_jobpostid on job_post_language_requirement;

alter table job_post_language_requirement drop foreign key fk_job_post_language_requirement_languageid;
drop index ix_job_post_language_requirement_languageid on job_post_language_requirement;

drop table if exists job_post_language_requirement;

alter table jobpost drop column jobPostMaxAge;