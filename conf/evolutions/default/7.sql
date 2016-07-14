# --- !Ups

alter table jobapplication add column locationpreferenceid bigint signed null;
alter table jobapplication add column jobsalaryisok int signed null;
alter table jobapplication add column jobtimingsisok int signed null;

alter table jobapplication add constraint fk_jobapplication_locationpreferenceid foreign key (locationpreferenceid) references locality (localityid) on delete restrict on update restrict;
create index ix_jobapplication_locationpreferenceid on jobapplication (locationpreferenceid);

# --- !Downs

alter table jobapplication drop foreign key fk_jobapplication_locationpreferenceid;
drop index ix_jobapplication_locationpreferenceid on jobapplication;

alter table jobapplication drop column locationpreferenceid;
alter table jobapplication drop column jobsalaryisok;
alter table jobapplication drop column jobtimingsisok;

