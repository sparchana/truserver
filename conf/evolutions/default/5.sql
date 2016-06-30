# --- !Ups
alter table candidate add column candidatelastwithdrawnsalary  bigint signed null;

alter table candidateeducation add column candidateeducationcompletionstatus int(1) null;

# --- !Downs

alter table candidate drop column candidatelastwithdrawnsalary;

alter table candidateeducation drop column candidateeducationcompletionstatus;