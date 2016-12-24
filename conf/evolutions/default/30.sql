# --- !Ups
create table recruiter_lead_to_company (
  company_lead_id               bigint auto_increment not null,
  company_lead_uuid             varchar(255) not null,
  company_lead_name             varchar(255),
  company_lead_type             bigint,
  company_lead_website          varchar(255),
  company_lead_industry         bigint,
  company_lead_create_timestamp timestamp not null default current_timestamp not null,
  company_lead_update_timestamp timestamp not null,
  constraint uq_recruiter_lead_to_company_company_lead_uuid unique (company_lead_uuid),
  constraint pk_recruiter_lead_to_company primary key (company_lead_id)
);
alter table recruiter_lead add column company_lead_id bigint null;
alter table recruiter_lead add constraint fk_recruiter_lead_company_lead_id foreign key (company_lead_id) references recruiter_lead_to_company (company_lead_id) on delete restrict on update restrict;
create index ix_recruiter_lead_company_lead_id on recruiter_lead (company_lead_id);

alter table recruiter_lead modify column recruiter_lead_name varchar(50) null;
alter table recruiter_lead modify column recruiter_lead_mobile varchar(13) null;
alter table recruiter_lead add column recruiter_lead_alt_number varchar(13) null;
alter table recruiter_lead add column recruiter_lead_email varchar(255) null;
alter table recruiter_lead MODIFY column recruiter_lead_requirement varchar(255) null;
alter table recruiter_lead add column recruiter_lead_source_type int not null;
alter table recruiter_lead add column recruiter_lead_source_name int not null;
alter table recruiter_lead add column recruiter_lead_source_date date null;

alter table recruiterleadtojobrole add column recruiter_lead_to_job_role_uuid varchar(255) not null;
alter table recruiterleadtojobrole add column job_interview_address varchar(255) null;
alter table recruiterleadtojobrole add column job_vacancies bigint null;
alter table recruiterleadtojobrole add column job_salary_min bigint null;
alter table recruiterleadtojobrole add column job_salary_max bigint null;
alter table recruiterleadtojobrole add column job_gender varchar(2) null;
alter table recruiterleadtojobrole add column job_detail_requirement varchar(255) null;

alter table recruiterleadtolocality add column recruiter_lead_to_locality_uuid varchar(255) not null;
alter table recruiterleadtolocality drop foreign key fk_recruiterleadtolocality_recruiter_lead_id;
drop index ix_recruiterleadtolocality_recruiter_lead_id on recruiterleadtolocality;
alter table recruiterleadtolocality drop column recruiter_lead_id;
alter table recruiterleadtolocality add column recruiter_lead_to_job_role_id bigint signed;

alter table recruiterleadtolocality add constraint fk_recruiterleadtolocality_recruiter_lead_to_job_role_id foreign key (recruiter_lead_to_job_role_id) references recruiterleadtojobrole (recruiter_lead_to_job_role_id) on delete restrict on update restrict;
create index ix_recruiterleadtolocality_recruiter_lead_to_job_role_id on recruiterleadtolocality (recruiter_lead_to_job_role_id);

# --- !Downs
drop table if exists recruiter_lead_to_company;

alter table recruiter_lead drop column company_lead_id;
alter table recruiter_lead modify column recruiter_lead_name varchar(50) not null;
alter table recruiter_lead modify column recruiter_lead_mobile varchar(13) not null;
alter table recruiter_lead drop column recruiter_lead_alt_number;
alter table recruiter_lead drop column recruiter_lead_email;
alter table recruiter_lead MODIFY column recruiter_lead_requirement varchar(50) not null;
alter table recruiter_lead drop column recruiter_lead_source_type;
alter table recruiter_lead drop column recruiter_lead_source_name;
alter table recruiter_lead drop column recruiter_lead_source_date;

alter table recruiter_lead drop foreign key fk_recruiter_lead_company_lead_id;
drop index ix_recruiter_lead_company_lead_id on recruiter_lead;

alter table recruiterleadtojobrole drop column recruiter_lead_to_job_role_uuid;
alter table recruiterleadtojobrole drop column job_interview_address;
alter table recruiterleadtojobrole drop column job_vacancies;
alter table recruiterleadtojobrole drop column job_salary_min;
alter table recruiterleadtojobrole drop column job_salary_max;
alter table recruiterleadtojobrole drop column job_gender;
alter table recruiterleadtojobrole drop column job_detail_requirement;

alter table recruiterleadtolocality drop column recruiter_lead_to_locality_uuid;
alter table recruiterleadtolocality add column recruiter_lead_id bigint signed;
alter table recruiterleadtolocality add constraint fk_recruiterleadtolocality_recruiter_lead_id foreign key (recruiter_lead_id) references recruiter_lead (recruiter_lead_id) on delete restrict on update restrict;
create index ix_recruiterleadtolocality_recruiter_lead_id on recruiterleadtolocality (recruiter_lead_id);
alter table recruiterleadtolocality drop column recruiter_lead_to_job_role_id;

alter table recruiterleadtolocality drop foreign key fk_recruiterleadtolocality_recruiter_lead_to_job_role_id;
drop index ix_recruiterleadtolocality_recruiter_lead_to_job_role_id on recruiterleadtolocality;
