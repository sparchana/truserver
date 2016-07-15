# --- !Ups
alter table candidate add column coldtableid int null;
alter table candidate add column candidatesecondmobile varchar(13) null;
alter table candidate add column candidatethirdmobile varchar(13) null;

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
alter table candidate drop column candidatesecondmobile;
alter table candidate drop column candidatethirdmobile;

drop table if exists coldtable;