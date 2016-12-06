# --- !Ups

alter table candidate add column candidate_android_token text null;

create table trudroid_feedback_reason (
  feedback_reason_id            int unsigned auto_increment not null,
  feedback_reason_name          text null,
  feedback_reason_type          int null,
  constraint pk_trudroid_feedback_reason primary key (feedback_reason_id)
);

create table trudroid_feedback (
  feedback_id                   int signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  feedback_comments             text null,
  feedback_rating               int null,
  candidateid                   bigint signed,
  constraint pk_trudroid_feedback primary key (feedback_id)
);

create table feedback_to_feedback_reason (
  feedback_to_feedback_reason_id bigint signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  feedback_id                   int signed,
  feedback_reason_id            int unsigned,
  constraint pk_feedback_to_feedback_reason primary key (feedback_to_feedback_reason_id)
);

alter table trudroid_feedback add constraint fk_trudroid_feedback_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_trudroid_feedback_candidateid on trudroid_feedback (candidateid);

alter table feedback_to_feedback_reason add constraint fk_feedback_to_feedback_reason_feedback_id foreign key (feedback_id) references trudroid_feedback (feedback_id) on delete restrict on update restrict;
create index ix_feedback_to_feedback_reason_feedback_id on feedback_to_feedback_reason (feedback_id);

alter table feedback_to_feedback_reason add constraint fk_feedback_to_feedback_reason_feedback_reason_id foreign key (feedback_reason_id) references trudroid_feedback_reason (feedback_reason_id) on delete restrict on update restrict;
create index ix_feedback_to_feedback_reason_feedback_reason_id on feedback_to_feedback_reason (feedback_reason_id);


# --- !Downs

alter table candidate drop column candidate_android_token;

alter table trudroid_feedback drop foreign key fk_trudroid_feedback_candidateid;
drop index ix_trudroid_feedback_candidateid on trudroid_feedback;

alter table feedback_to_feedback_reason drop foreign key fk_feedback_to_feedback_reason_feedback_id;
drop index ix_feedback_to_feedback_reason_feedback_id on feedback_to_feedback_reason;

alter table feedback_to_feedback_reason drop foreign key fk_feedback_to_feedback_reason_feedback_reason_id;
drop index ix_feedback_to_feedback_reason_feedback_reason_id on feedback_to_feedback_reason;

drop table if exists trudroid_feedback_reason;
drop table if exists trudroid_feedback;
drop table if exists feedback_to_feedback_reason;