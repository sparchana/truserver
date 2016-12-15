# --- !Ups

alter table interview_details add column interview_building_no text null;
alter table interview_details add column interview_address text null;
alter table interview_details add column interview_landmark text null;

# --- !Downs

alter table interview_details drop column interview_building_no;
alter table interview_details drop column interview_address;
alter table interview_details drop column interview_landmark;