# --- !Ups

alter table jobpost add column resume_application_date date null;

# --- !Downs

alter table jobpost drop column resume_application_date;