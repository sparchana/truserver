# --- !Ups

alter table candidate add column candidate_android_token text null;

create table candidate_feedback_reason (
  reason_id                     int unsigned auto_increment not null,
  reason_name                   text null,
  reason_type                   int null,
  constraint pk_candidate_feedback_reason primary key (reason_id)
);

create table candidate_feedback (
  feedback_id                   int signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  feedback_comments             text null,
  feedback_rating               int null,
  candidateid                   bigint signed,
  feedback_channel              int signed not null,
  constraint pk_candidate_feedback primary key (feedback_id)
);

create table feedback_to_reason (
  feedback_to_reason_id         bigint signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  feedback_id                   int signed,
  reason_id                     int unsigned,
  constraint pk_feedback_to_reason primary key (feedback_to_reason_id)
);

alter table candidate_feedback add constraint fk_candidate_feedback_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_feedback_candidateid on candidate_feedback (candidateid);

alter table feedback_to_reason add constraint fk_feedback_to_reason_feedback_id foreign key (feedback_id) references candidate_feedback (feedback_id) on delete restrict on update restrict;
create index ix_feedback_to_reason_feedback_id on feedback_to_reason (feedback_id);

alter table feedback_to_reason add constraint fk_feedback_to_reason_reason_id foreign key (reason_id) references candidate_feedback_reason (reason_id) on delete restrict on update restrict;
create index ix_feedback_to_reason_reason_id on feedback_to_reason (reason_id);

# --- !Downs

alter table candidate drop column candidate_android_token;

alter table candidate_feedback drop foreign key fk_candidate_feedback_candidateid;
drop index ix_candidate_feedback_candidateid on candidate_feedback;

alter table feedback_to_reason drop foreign key fk_feedback_to_reason_feedback_id;
drop index ix_feedback_to_reason_feedback_id on feedback_to_reason;

alter table feedback_to_reason drop foreign key fk_feedback_to_reason_reason_id;
drop index ix_feedback_to_reason_reason_id on feedback_to_reason;

drop table if exists candidate_feedback_reason;
drop table if exists candidate_feedback;
drop table if exists feedback_to_reason;