# --- !Ups

alter table jobapplication add column partner_id bigint signed;

alter table jobapplication add constraint fk_jobapplication_partner_id foreign key (partner_id) references partner (partner_id) on delete restrict on update restrict;
alter table jobapplication add index ix_jobapplication_partner_id (partner_id)

# --- !Downs

alter table jobapplication drop foreign key fk_jobapplication_partner_id;
drop index ix_jobapplication_partner_id on jobapplication;

alter table jobapplication drop column partner_id;