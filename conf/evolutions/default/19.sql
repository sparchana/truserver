# --- !Ups

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

alter table related_jobrole drop foreign key fk_related_jobrole_job_role_id;
drop index ix_related_jobrole_job_role_id on related_jobrole;

alter table related_jobrole drop foreign key fk_related_jobrole_related_job_role_id;
drop index ix_related_jobrole_related_job_role_id on related_jobrole;

alter table candidate_assessment_attempt drop column ca_attempt_uuid;

drop table if exists related_jobrole;