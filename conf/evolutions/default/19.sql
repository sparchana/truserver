# --- !Ups

create table related_jobrole (
  related_jobrole_id            bigint signed auto_increment not null,
  jobroleid                     bigint signed,
  weight                        double(2, 2) null,
  constraint pk_related_jobrole primary key (related_jobrole_id)
);

alter table related_jobrole add constraint fk_related_jobrole_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_related_jobrole_jobroleid on related_jobrole (jobroleid);

# --- !Downs

alter table related_jobrole drop foreign key fk_related_jobrole_jobroleid;
drop index ix_related_jobrole_jobroleid on related_jobrole;

drop table if exists related_jobrole;