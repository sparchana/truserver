# --- !Ups
alter table lead add column leadsourceid int null;

create table leadsource (
  leadsourceid                  int signed null auto_increment,
  leadsourcename                varchar(255) null,
  constraint pk_leadsource primary key (leadsourceid)
);

alter table lead add constraint fk_lead_leadsourceid foreign key (leadsourceid) references leadsource (leadsourceid) on delete restrict on update restrict;
create index ix_lead_leadsourceid on lead (leadsourceid);

# --- !Downs
alter table lead drop foreign key fk_lead_leadsourceid;
drop index ix_lead_leadsourceid on lead;

drop table if exists leadsource;

alter table lead drop column leadsourceid