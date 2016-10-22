# --- !Ups

alter table recruiter_credit_history add column recruiter_credits_added_by varchar(50) not null;

alter table profile_requirement drop column profile_requirement_uuid;

# --- !Downs

alter table recruiter_credit_history drop column recruiter_credits_added_by;

alter table profile_requirement add column   profile_requirement_uuid  varchar(255) not null;
