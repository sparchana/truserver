# --- !Ups
alter table recruiter_lead_to_company add constraint fk_recruiter_lead_to_company_company_lead_type foreign key (company_lead_type) references companytype (companytypeid) on delete restrict on update restrict;
create index ix_recruiter_lead_to_company_company_lead_type on recruiter_lead_to_company (company_lead_type);

# --- !Downs
alter table recruiter_lead_to_company drop foreign key fk_recruiter_lead_to_company_company_lead_type;
drop index ix_recruiter_lead_to_company_company_lead_type on recruiter_lead_to_company;
