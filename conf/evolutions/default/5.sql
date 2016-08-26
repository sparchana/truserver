# --- !Ups

alter table candidate add column candidatelastwithdrawnsalary  bigint signed null;

alter table candidate add column candidateexperienceletter  bit null;

alter table candidateeducation add column candidateeducationcompletionstatus int(1) null;

alter table jobhistory add column currentJob bit null;

alter table languageknown add column understanding int(1) null;

alter table languageknown add column readwrite int(1) null;

# --- !Downs

alter table candidate drop column candidatelastwithdrawnsalary;

alter table candidate drop column candidateexperienceletter;

alter table candidateeducation drop column candidateeducationcompletionstatus;

alter table jobhistory drop column currentJob;

alter table languageknown drop column understanding;

alter table languageknown drop column readwrite;
