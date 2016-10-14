# --- !Ups

create table assessment_question (
  assessment_question_id        int unsigned auto_increment not null,
  question_text                 text null,
  assessment_question_type_id   int unsigned null,
  optiona                       text null,
  optionb                       text null,
  optionc                       text null,
  optiond                       text null,
  optione                       text null,
  answer                        text null,
  jobroleid                     bigint signed null,
  skillid                       int signed null,
  constraint pk_assessment_question primary key (assessment_question_id)
);

alter table assessment_question add constraint fk_assessment_question_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_assessment_question_jobroleid on assessment_question (jobroleid);

alter table assessment_question add constraint fk_assessment_question_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_assessment_question_skillid on assessment_question (skillid);

create table assessment_question_type (
  assessment_question_type_id   int unsigned auto_increment not null,
  assessment_question_type_title varchar(255) null,
  constraint pk_assessment_question_type primary key (assessment_question_type_id)
);

alter table assessment_question add constraint fk_assessment_question_assessment_question_type_id foreign key (assessment_question_type_id) references assessment_question_type (assessment_question_type_id) on delete restrict on update restrict;
create index ix_assessment_question_assessment_question_type_id on assessment_question (assessment_question_type_id);


create table candidate_assessment_attempt (
  ca_attempt_id                 bigint signed auto_increment not null,
  candidateid                   bigint signed,
  jobpostid                     bigint signed,
  jobroleid                     bigint signed,
  result                        text null,
  constraint pk_candidate_assessment_attempt primary key (ca_attempt_id)
);

alter table candidate_assessment_attempt add constraint fk_candidate_assessment_attempt_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_assessment_attempt_candidateid on candidate_assessment_attempt (candidateid);

alter table candidate_assessment_attempt add constraint fk_candidate_assessment_attempt_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_candidate_assessment_attempt_jobpostid on candidate_assessment_attempt (jobpostid);

alter table candidate_assessment_attempt add constraint fk_candidate_assessment_attempt_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_candidate_assessment_attempt_jobroleid on candidate_assessment_attempt (jobroleid);

# --- !Downs

alter table assessment_question drop foreign key fk_assessment_question_jobroleid;
drop index ix_assessment_question_jobroleid on assessment_question;

alter table assessment_question drop foreign key fk_assessment_question_skillid;
drop index ix_assessment_question_skillid on assessment_question;

alter table assessment_question drop foreign key fk_assessment_question_assessment_question_type_id;
drop index ix_assessment_question_assessment_question_type_id on assessment_question;

alter table candidate_assessment_attempt drop foreign key fk_candidate_assessment_attempt_candidateid;
drop index ix_candidate_assessment_attempt_candidateid on candidate_assessment_attempt;

alter table candidate_assessment_attempt drop foreign key fk_candidate_assessment_attempt_jobpostid;
drop index ix_candidate_assessment_attempt_jobpostid on candidate_assessment_attempt;

alter table candidate_assessment_attempt drop foreign key fk_candidate_assessment_attempt_jobroleid;
drop index ix_candidate_assessment_attempt_jobroleid on candidate_assessment_attempt;


drop table if exists assessment_question;

drop table if exists assessment_question_type;

drop table if exists candidate_assessment_attempt;
