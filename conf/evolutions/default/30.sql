# --- !Ups

alter table job_post_workflow add column scheduled_interview_time_slot bigint signed;
alter table job_post_workflow add column scheduled_interview_date date null;
alter table job_post_workflow add column interview_location_lat double null;
alter table job_post_workflow add column interview_location_lng double null;
alter table job_post_workflow add column channel int null;

# --- !Downs

alter table job_post_workflow drop column scheduled_interview_time_slot;
alter table job_post_workflow drop column scheduled_interview_date;
alter table job_post_workflow drop column channel;
alter table job_post_workflow drop column interview_location_lat;
alter table job_post_workflow drop column interview_location_lng;
