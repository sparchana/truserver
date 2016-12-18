# --- !Ups

alter table interview_details drop column latitude;
alter table interview_details drop column longitude;
alter table interview_details drop column placeid;
alter table interview_details drop column reviewapplication;

alter table interview_details drop column interview_building_no;
alter table interview_details drop column interview_address;
alter table interview_details drop column interview_landmark;

alter table jobpost add column interview_building_no text null;
alter table jobpost add column interview_landmark text null;
alter table jobpost add column placeid double(10,6) null;
alter table jobpost add column reviewapplication int(1) null;

# --- !Downs

alter table interview_details add column latitude double(10,6) null;
alter table interview_details add column longitude double(10,6) null;
alter table interview_details add column placeid double(10,6) null;
alter table interview_details add column reviewapplication int(1) null;

alter table interview_details add column interview_building_no text null;
alter table interview_details add column interview_address text null;
alter table interview_details add column interview_landmark text null;

alter table jobpost drop column interview_building_no;
alter table jobpost drop column interview_landmark;
alter table jobpost drop column placeid;
alter table jobpost drop column reviewapplication;