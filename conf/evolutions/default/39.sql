# --- !Ups
alter table recruiterprofile add column recruiter_access_level int(2) signed not null DEFAULT 0;

# --- !Downs

alter table recruiterprofile drop column recruiter_access_level;
