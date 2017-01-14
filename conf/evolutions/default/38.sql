# --- !Ups

alter table jobpost add column resume_application_date date null;

alter table recruiter_credit_history add column recruiter_credit_pack_no int signed null;
alter table recruiter_credit_history add column expiry_date date null;
alter table recruiter_credit_history add column credit_is_expired int signed null;
alter table recruiter_credit_history add column is_latest int signed null;

# --- !Downs

alter table jobpost drop column resume_application_date;

alter table recruiter_credit_history drop column recruiter_credit_pack_no;
alter table recruiter_credit_history drop column expiry_date;
alter table recruiter_credit_history drop column credit_is_expired;
alter table recruiter_credit_history drop column is_latest;