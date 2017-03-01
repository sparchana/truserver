# --- !Ups

alter table sms_report add column partner_id  bigint signed null;
alter table partner_to_company add column foreign_employee_id  varchar(255) null;

alter table sms_report add constraint fk_sms_report_partner_id foreign key (partner_id) references partner (partner_id) on delete restrict on update restrict;
create index ix_sms_report_partner_id on sms_report (partner_id);


# --- !Downs

alter table sms_report drop column partner_id;
alter table partner_to_company drop column foreign_employee_id;


alter table sms_report drop foreign key fk_sms_report_partner_id;
drop index ix_sms_report_partner_id on sms_report;