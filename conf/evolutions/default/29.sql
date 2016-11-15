# --- !Ups

alter table idproofreference add column idproofnumber varchar(255) null;

alter table interview_details add column latitude double(10,6) null;
alter table interview_details add column longitude double(10,6) null;
alter table interview_details add column placeid double(10,6) null;

# --- !Downs

alter table idproofreference drop column idproofnumber;

alter table interview_details drop column latitude;
alter table interview_details drop column longitude;
alter table interview_details drop column placeid;
