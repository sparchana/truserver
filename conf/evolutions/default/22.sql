# --- !Ups

create table credit_history (
  credit_history_id             int signed auto_increment not null,
  credits_available             int signed null,
  credits_used                  int signed null,
  credit_history_create_timestamp timestamp not null default current_timestamp,
  recruiterprofileid            bigint signed,
  recruitercreditcategory       bigint signed,
  constraint pk_credit_history primary key (credit_history_id)
);

create table recruiter_credit_category (
  recruiter_credit_category_id  bigint signed auto_increment not null,
  recruiter_credit_type         varchar(50) not null,
  recruiter_credit_unit_price   int signed null,
  constraint pk_recruiter_credit_category primary key (recruiter_credit_category_id)
);

create table recruiter_payment (
  recruiter_payment_id          bigint signed auto_increment not null,
  recruiter_payment_amount      bigint unsigned not null,
  recruiter_payment_credit_unit_price bigint unsigned null,
  recruiter_payment_mode        int signed null,
  recruiterprofileid            bigint signed,
  constraint pk_recruiter_payment primary key (recruiter_payment_id)
);

alter table recruiter_payment add constraint fk_recruiter_payment_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_payment_recruiterprofileid on recruiter_payment (recruiterprofileid);

alter table recruiterprofile add column recruiterinterviewunlockcredits int signed null;
alter table recruiterprofile add column recruitercandidateunlockcredits int signed null;

alter table credit_history add constraint fk_credit_history_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_credit_history_recruiterprofileid on credit_history (recruiterprofileid);

alter table credit_history add constraint fk_credit_history_recruitercreditcategory foreign key (recruitercreditcategory) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_credit_history_recruitercreditcategory on credit_history (recruitercreditcategory);

# --- !Downs

alter table credit_history drop foreign key fk_credit_history_recruiterprofileid;
drop index ix_credit_history_recruiterprofileid on credit_history;

alter table credit_history drop foreign key fk_credit_history_recruitercreditcategory;
drop index ix_credit_history_recruitercreditcategory on credit_history;

alter table recruiter_payment drop foreign key fk_recruiter_payment_recruiterprofileid;
drop index ix_recruiter_payment_recruiterprofileid on recruiter_payment;

alter table recruiterprofile drop column recruiterinterviewunlockcredits;
alter table recruiterprofile drop column recruitercandidateunlockcredits;

drop table if exists recruiter_credit_category;
drop table if exists credit_history;
drop table if exists recruiter_payment;
