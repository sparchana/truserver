# --- !Ups

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

create table candidate_to_company (
  candidate_to_company_id       int signed auto_increment not null,
  creation_timestamp            timestamp not null default current_timestamp,
  candidateid                   bigint signed,
  companyid                     bigint signed,
  constraint pk_candidate_to_company primary key (candidate_to_company_id)
);

alter table candidate_to_company add constraint fk_candidate_to_company_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_to_company_candidateid on candidate_to_company (candidateid);

alter table candidate_to_company add constraint fk_candidate_to_company_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_candidate_to_company_companyid on candidate_to_company (companyid);


alter table candidate add column candidateisprivate int signed not null default 0;
alter table jobpost add column jobpostisprivate int signed not null default 0;
alter table company add column companycode varchar(20) null;
alter table partner add column companyid bigint signed;

alter table partner add constraint fk_partner_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_partner_companyid on partner (companyid);

# --- !Downs

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

alter table candidate_to_company drop foreign key fk_candidate_to_company_candidateid;
drop index ix_candidate_to_company_candidateid on candidate_to_company;

alter table candidate_to_company drop foreign key fk_candidate_to_company_companyid;
drop index ix_candidate_to_company_companyid on candidate_to_company;

alter table partner drop foreign key fk_partner_companyid;
drop index ix_partner_companyid on partner;

alter table partner drop column companyid;
alter table company drop column companycode;
alter table candidate drop column candidateisprivate;
alter table jobpost drop column jobpostisprivate;

drop table if exists sms_report;
drop table if exists sms_delivery_status;
drop table if exists candidate_to_company;
