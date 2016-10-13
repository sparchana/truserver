# --- !Ups

create table recruiter_credit_history (
  recruiter_credit_history_id   int signed auto_increment not null,
  recruiter_credit_history_uuid varchar(255) not null not null,
  recruiter_credits_available   int signed null,
  recruiter_credits_used        int signed null,
  create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  recruitercreditcategory       bigint signed,
  constraint pk_recruiter_credit_history primary key (recruiter_credit_history_id)
);

create table recruiter_credit_category (
  recruiter_credit_category_id  bigint signed auto_increment not null,
  recruiter_credit_type         varchar(50) not null,
  recruiter_credit_unit_price   int signed null,
  constraint pk_recruiter_credit_category primary key (recruiter_credit_category_id)
);

create table recruiter_payment (
  recruiter_payment_id          bigint signed auto_increment not null,
  recruiter_payment_uuid        varchar(255) not null not null,
  recruiter_payment_amount      bigint unsigned not null,
  recruiter_payment_credit_unit_price bigint unsigned null,
  recruiter_payment_mode        int signed null,
  create_timestamp timestamp not null default current_timestamp,
  recruiter_credit_category_id  bigint signed,
  recruiterprofileid            bigint signed,
  constraint pk_recruiter_payment primary key (recruiter_payment_id)
);

create table recruiter_to_candidate_unlocked (
  recruiter_to_candidate_unlocked_id int signed auto_increment not null,
  recruiter_to_candidate_unlocked_uuid        varchar(255) not null not null,
  create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  candidateid                   bigint signed,
  constraint pk_recruiter_to_candidate_unlocked primary key (recruiter_to_candidate_unlocked_id)
);

alter table recruiter_to_candidate_unlocked add constraint fk_recruiter_to_candidate_unlocked_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_to_candidate_unlocked_recruiterprofileid on recruiter_to_candidate_unlocked (recruiterprofileid);

alter table recruiter_to_candidate_unlocked add constraint fk_recruiter_to_candidate_unlocked_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_recruiter_to_candidate_unlocked_candidateid on recruiter_to_candidate_unlocked (candidateid);

alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile add column recruiterlinkedinprofile varchar(60) null;

alter table recruiter_payment add constraint fk_recruiter_payment_recruiter_credit_category_id foreign key (recruiter_credit_category_id) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_recruiter_payment_recruiter_credit_category_id on recruiter_payment (recruiter_credit_category_id);

alter table recruiter_payment add constraint fk_recruiter_payment_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_payment_recruiterprofileid on recruiter_payment (recruiterprofileid);

alter table recruiter_credit_history add constraint fk_recruiter_credit_history_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_credit_history_recruiterprofileid on recruiter_credit_history (recruiterprofileid);

alter table recruiter_credit_history add constraint fk_recruiter_credit_history_recruitercreditcategory foreign key (recruitercreditcategory) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_recruiter_credit_history_recruitercreditcategory on recruiter_credit_history (recruitercreditcategory);

# --- !Downs

alter table recruiterprofile drop column recruiterlinkedinprofile;
alter table recruiterprofile add column recruiterlinkedinprofile bigint signed null;

alter table recruiter_to_candidate_unlocked drop foreign key fk_recruiter_to_candidate_unlocked_recruiterprofileid;
drop index ix_recruiter_to_candidate_unlocked_recruiterprofileid on recruiter_to_candidate_unlocked;

alter table recruiter_to_candidate_unlocked drop foreign key fk_recruiter_to_candidate_unlocked_candidateid;
drop index ix_recruiter_to_candidate_unlocked_candidateid on recruiter_to_candidate_unlocked;

alter table recruiter_credit_history drop foreign key fk_recruiter_credit_history_recruiterprofileid;
drop index ix_recruiter_credit_history_recruiterprofileid on recruiter_credit_history;

alter table recruiter_credit_history drop foreign key fk_recruiter_credit_history_recruitercreditcategory;
drop index ix_recruiter_credit_history_recruitercreditcategory on recruiter_credit_history;

alter table recruiter_payment drop foreign key fk_recruiter_payment_recruiter_credit_category_id;
drop index ix_recruiter_payment_recruiter_credit_category_id on recruiter_payment;

alter table recruiter_payment drop foreign key fk_recruiter_payment_recruiterprofileid;
drop index ix_recruiter_payment_recruiterprofileid on recruiter_payment;

drop table if exists recruiter_credit_category;
drop table if exists recruiter_credit_history;
drop table if exists recruiter_payment;
drop table if exists recruiter_to_candidate_unlocked;