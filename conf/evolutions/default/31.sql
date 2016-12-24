# --- !Ups
create table recruiter_lead_status (
  recruiterleadstatusid         bigint auto_increment not null,
  recruiterleadstatusname       varchar(20) not null,
  constraint pk_recruiter_lead_status primary key (recruiterleadstatusid)
);


# --- !Downs
drop table if exists recruiter_lead_status;
