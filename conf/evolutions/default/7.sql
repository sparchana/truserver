# --- !Ups
alter table candidate add column coldtableid int null;

create table coldtable (
  coldtableid                   int signed auto_increment not null,
  reason                        text null,
  duration                      int signed null,
  createtimestamp               timestamp not null default current_timestamp,
  updatetimestamp               timestamp null,
  constraint pk_coldtable primary key (coldtableid)
);

alter table candidate add constraint fk_candidate_coldtableid foreign key (coldtableid) references coldtable (coldtableid) on delete restrict on update restrict;


# --- !Downs

alter table candidate drop foreign key fk_candidate_coldtableid;

alter table candidate drop column coldtableid;

drop table if exists coldtable;