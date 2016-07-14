# --- !Ups

alter table jobapplication add column prescreenlocation bigint signed null;
alter table jobapplication add column prescreensalary int signed null;
alter table jobapplication add column prescreentimings int signed null;

alter table jobapplication add constraint fk_jobapplication_prescreenlocation foreign key (prescreenlocation) references locality (localityid) on delete restrict on update restrict;
create index ix_jobapplication_prescreenlocation on jobapplication (prescreenlocation);

# --- !Downs

alter table jobapplication drop foreign key fk_jobapplication_prescreenlocation;
drop index ix_jobapplication_prescreenlocation on jobapplication;

alter table jobapplication drop column prescreenlocation;
alter table jobapplication drop column prescreentimings;
alter table jobapplication drop column prescreensalary;

