# --- !Ups

alter table recruiter_credit_history add column recruiter_credits_added_by varchar(50) not null;

# --- !Downs

alter table recruiter_credit_history drop column recruiter_credits_added_by;