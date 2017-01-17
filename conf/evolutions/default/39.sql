# --- !Ups

alter table company add column companyCode bigint signed null;
alter table partner add column companyid bigint signed;

alter table partner add constraint fk_partner_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_partner_companyid on partner (companyid);

# --- !Downs

alter table partner drop foreign key fk_partner_companyid;
drop index ix_partner_companyid on partner;

alter table partner drop column companyid;
alter table company drop column companyCode;