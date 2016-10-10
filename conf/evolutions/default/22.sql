# --- !Ups

create table credit_history (
  credit_history_id             int signed auto_increment not null,
  credit_type                   int signed null,
  credits_available             int signed null,
  credits_used                  int signed null,
  credit_history_create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  constraint pk_credit_history primary key (credit_history_id)
);

create table recruiter_credit_category (
  recruiter_credit_category_id  bigint signed auto_increment not null,
  recruiter_credit_type         int signed null,
  recruiter_credit_mode         int signed null,
  recruiter_credit_unit_price   int signed null,
  constraint pk_recruiter_credit_category primary key (recruiter_credit_category_id)
);

alter table recruiterprofile add column recruiterinterviewunlockcredits int signed null;
alter table recruiterprofile add column recruitercandidateunlockcredits int signed null;

alter table credit_history add constraint fk_credit_history_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_credit_history_recruiterprofileid on credit_history (recruiterprofileid);

# --- !Downs

alter table credit_history drop foreign key fk_credit_history_recruiterprofileid;
drop index ix_credit_history_recruiterprofileid on credit_history;

alter table recruiterprofile drop column recruiterinterviewunlockcredits;
alter table recruiterprofile drop column recruitercandidateunlockcredits;

drop table if exists recruiter_credit_category;
drop table if exists credit_history;
