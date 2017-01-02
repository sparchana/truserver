# --- !Ups

create table recruiter_credit_pack (
  recruiter_credit_pack_id      int signed auto_increment not null,
  recruiter_credit_pack_uuid    varchar(255) not null not null,
  recruiter_credit_pack_no      int signed,
  recruiterprofileid            bigint signed,
  recruitercreditcategory       bigint signed,
  credits_available             int signed null,
  credits_used                  int signed null,
  create_timestamp              timestamp not null default current_timestamp,
  expiry_date                   date null,
  credit_is_expired             int signed null,
  constraint uq_recruiter_credit_pack_recruiter_credit_pack_uuid unique (recruiter_credit_pack_uuid),
  constraint pk_recruiter_credit_pack primary key (recruiter_credit_pack_id)
);

alter table recruiter_credit_pack add constraint fk_recruiter_credit_pack_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_recruiter_credit_pack_recruiterprofileid on recruiter_credit_pack (recruiterprofileid);

alter table recruiter_credit_pack add constraint fk_recruiter_credit_pack_recruitercreditcategory foreign key (recruitercreditcategory) references recruiter_credit_category (recruiter_credit_category_id) on delete restrict on update restrict;
create index ix_recruiter_credit_pack_recruitercreditcategory on recruiter_credit_pack (recruitercreditcategory);

# --- !Downs

alter table recruiter_credit_pack drop foreign key fk_recruiter_credit_pack_recruiterprofileid;
drop index ix_recruiter_credit_pack_recruiterprofileid on recruiter_credit_pack;

alter table recruiter_credit_pack drop foreign key fk_recruiter_credit_pack_recruitercreditcategory;
drop index ix_recruiter_credit_pack_recruitercreditcategory on recruiter_credit_pack;

drop table if exists recruiter_credit_pack;