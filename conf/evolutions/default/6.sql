# --- !Ups

alter table jobpost add column jobrecruiterid bigint signed null;
alter table recruiterprofile add column reccompany bigint signed null;

alter table jobpost add constraint fk_jobpost_jobrecruiterid foreign key (jobrecruiterid) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;
create index ix_jobpost_jobrecruiterid on jobpost (jobrecruiterid);

alter table recruiterprofile add constraint fk_recruiterprofile_reccompany foreign key (reccompany) references company (companyid) on delete restrict on update restrict;
create index ix_recruiterprofile_reccompany on recruiterprofile (reccompany);

# --- !Downs

alter table recruiterprofile drop foreign key fk_recruiterprofile_reccompany;
drop index ix_recruiterprofile_reccompany on recruiterprofile;

alter table jobpost drop foreign key fk_jobpost_jobrecruiterid;
drop index ix_jobpost_jobrecruiterid on jobpost;

alter table recruiterprofile drop column reccompany;
alter table jobpost drop column jobrecruiterid;
