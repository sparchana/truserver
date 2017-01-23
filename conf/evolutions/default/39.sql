# --- !Ups

alter table partner_to_candidate drop foreign key fk_partner_to_candidate_candidate_candidateid;
alter table partner_to_candidate drop index uq_partner_to_candidate_candidate_candidateid;

alter table partner_to_candidate add constraint fk_partner_to_candidate_candidate_candidateid foreign key (candidate_candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_partner_to_candidate_candidate_candidateid on partner_to_candidate (candidate_candidateid);

create table sms_delivery_status (
  status_id                     int signed auto_increment not null,
  status_name                   varchar(50) null,
  constraint pk_sms_delivery_status primary key (status_id)
);

create table sms_report (
  sms_report_id                 int signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  sms_text                      text null,
  sms_scheduler_id              text null,
  companyid                     bigint signed,
  recruiterprofileid            bigint signed,
  candidateid                   bigint signed,
  jobpostid                     bigint signed,
  smsdeliverystatus             int signed,
  constraint pk_sms_report primary key (sms_report_id)
);

alter table sms_report add constraint fk_sms_report_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_sms_report_companyid on sms_report (companyid);

alter table sms_report add constraint fk_sms_report_recruiterprofileid foreign key (recruiterprofileid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_sms_report_recruiterprofileid on sms_report (recruiterprofileid);

alter table sms_report add constraint fk_sms_report_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_sms_report_candidateid on sms_report (candidateid);

alter table sms_report add constraint fk_sms_report_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_sms_report_jobpostid on sms_report (jobpostid);

alter table sms_report add constraint fk_sms_report_smsdeliverystatus foreign key (smsdeliverystatus) references sms_delivery_status (status_id) on delete restrict on update restrict;
create index ix_sms_report_smsdeliverystatus on sms_report (smsdeliverystatus);

create table partner_to_company (
  partner_to_company_id         bigint signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  partner_id                    bigint signed,
  companyid                     bigint signed,
  verification_status           int(2) signed not null default 0,
  constraint pk_partner_to_company primary key (partner_to_company_id)
);

alter table partner_to_company add constraint fk_partner_to_company_partner_id foreign key (partner_id) references partner (partner_id) on delete restrict on update restrict;
create index ix_partner_to_company_partner_id on partner_to_company (partner_id);

alter table partner_to_company add constraint fk_partner_to_company_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_partner_to_company_companyid on partner_to_company (companyid);

alter table company add column companycode varchar(20) null;

alter table jobpost add column job_post_access_level int(2) signed not null DEFAULT 0;
alter table recruiterprofile add column recruiter_access_level int(2) signed not null DEFAULT 0;
alter table candidate add column candidate_access_level int(2) signed not null DEFAULT 0;

# --- !Downs

alter table partner_to_candidate drop foreign key fk_partner_to_candidate_candidate_candidateid;
drop index ix_partner_to_candidate_candidate_candidateid on partner_to_candidate;

alter table partner_to_candidate add constraint fk_partner_to_candidate_candidate_candidateid foreign key (candidate_candidateid) references candidate (candidateid) on delete restrict on update restrict;

ALTER TABLE partner_to_candidate ADD constraint uq_partner_to_candidate_candidate_candidateid unique (candidate_candidateid);

alter table partner_to_company drop foreign key fk_partner_to_company_partner_id;
drop index ix_partner_to_company_partner_id on partner_to_company;

alter table partner_to_company drop foreign key fk_partner_to_company_companyid;
drop index ix_partner_to_company_companyid on partner_to_company;

alter table sms_report drop foreign key fk_sms_report_companyid;
drop index ix_sms_report_companyid on sms_report;

alter table sms_report drop foreign key fk_sms_report_recruiterprofileid;
drop index ix_sms_report_recruiterprofileid on sms_report;

alter table sms_report drop foreign key fk_sms_report_candidateid;
drop index ix_sms_report_candidateid on sms_report;

alter table sms_report drop foreign key fk_sms_report_jobpostid;
drop index ix_sms_report_jobpostid on sms_report;

alter table sms_report drop foreign key fk_sms_report_smsdeliverystatus;
drop index ix_sms_report_smsdeliverystatus on sms_report;

alter table company drop column companycode;

alter table jobpost drop column job_post_access_level ;
alter table recruiterprofile drop column recruiter_access_level;
alter table candidate drop column candidate_access_level;

drop table if exists sms_report;
drop table if exists sms_delivery_status;
drop table if exists partner_to_company;