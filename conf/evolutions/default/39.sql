# --- !Ups

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
drop table if exists candidate_to_company;