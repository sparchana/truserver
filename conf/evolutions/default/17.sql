# --- !Ups

create table candidate_assessment_response (
  ca_response_id                bigint signed auto_increment not null,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  ca_attempt_id                 bigint signed,
  assessment_question_id        int unsigned,
  candidate_answer              text null,
  score                         int null,
  constraint pk_candidate_assessment_response primary key (ca_response_id)
);

alter table candidate_assessment_response add constraint fk_candidate_assessment_response_ca_attempt_id foreign key (ca_attempt_id) references candidate_assessment_attempt (ca_attempt_id) on delete restrict on update restrict;
create index ix_candidate_assessment_response_ca_attempt_id on candidate_assessment_response (ca_attempt_id);

alter table candidate_assessment_response add constraint fk_candidate_assessment_response_assessment_question_id foreign key (assessment_question_id) references assessment_question (assessment_question_id) on delete restrict on update restrict;
create index ix_candidate_assessment_response_assessment_question_id on candidate_assessment_response (assessment_question_id);

alter table candidate_assessment_attempt drop column result;
alter table candidate_assessment_attempt add result float null;

alter table candidate_assessment_attempt add create_timestamp timestamp not null default current_timestamp;

# --- !Downs

alter table candidate_assessment_response drop foreign key fk_candidate_assessment_response_ca_attempt_id;
drop index ix_candidate_assessment_response_ca_attempt_id on candidate_assessment_response;

alter table candidate_assessment_response drop foreign key fk_candidate_assessment_response_assessment_question_id;
drop index ix_candidate_assessment_response_assessment_question_id on candidate_assessment_response;

drop table if exists candidate_assessment_response;

alter table candidate_assessment_attempt drop column result;
alter table candidate_assessment_attempt add result text null;

alter table candidate_assessment_attempt  drop column  create_timestamp;
