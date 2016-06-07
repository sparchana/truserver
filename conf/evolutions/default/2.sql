# --- !Ups
alter table candidate DROP COLUMN candidategender;
alter table candidate ADD COLUMN candidategender int(1) null;
alter table candidate DROP COLUMN CandidateTotalExperience;
alter table candidate ADD COLUMN CandidateTotalExperience int signed null;
alter table candidate DROP COLUMN CandidateSalarySlip;
alter table candidate ADD COLUMN CandidateSalarySlip int signed null;
alter table candidate DROP COLUMN CandidateAppointmentLetter;
alter table candidate ADD COLUMN CandidateAppointmentLetter int signed null;
alter table candidate DROP COLUMN CandidateIsEmployed;
alter table candidate ADD COLUMN CandidateIsEmployed int signed null;
alter table candidate DROP COLUMN CandidateMaritalStatus;
alter table candidate ADD COLUMN CandidateMaritalStatus int signed null;
alter table candidate DROP COLUMN CandidateAge;
alter table candidate ADD COLUMN CandidateAge int signed null;


# --- !Downs
alter table candidate DROP COLUMN candidategender;
alter table candidate ADD COLUMN candidategender int(1) null default 0;
alter table candidate DROP COLUMN CandidateTotalExperience;
alter table candidate ADD COLUMN CandidateTotalExperience int signed null default 0.00;
alter table candidate DROP COLUMN CandidateSalarySlip;
alter table candidate ADD COLUMN CandidateSalarySlip int signed not null default 0;
alter table candidate DROP COLUMN CandidateAppointmentLetter;
alter table candidate ADD COLUMN CandidateAppointmentLetter int signed not null default 0;
alter table candidate DROP COLUMN CandidateIsEmployed;
alter table candidate ADD COLUMN CandidateIsEmployed int not null;
alter table candidate DROP COLUMN CandidateMaritalStatus;
alter table candidate ADD COLUMN CandidateMaritalStatus  int null;
alter table candidate DROP COLUMN CandidateAge;
alter table candidate ADD COLUMN CandidateAge int signed not null;