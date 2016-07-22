# --- !Ups
alter table candidate add column candidatestatusdetailid int null;
alter table candidate add column candidatesecondmobile varchar(13) null;
alter table candidate add column candidatethirdmobile varchar(13) null;

create table reason (
  reasonid                      int unsigned auto_increment not null,
  reasonname                    text null,
  constraint pk_reason primary key (reasonid)
);

create table candidatestatusdetail (
  candidatestatusdetailid       int signed auto_increment not null,
  reasonid                      int unsigned,
  createtimestamp               timestamp not null default current_timestamp,
  updatetimestamp               timestamp null,
  statusexpirydate              date null,
  constraint pk_candidatestatusdetail primary key (candidatestatusdetailid)
);

alter table candidate add constraint fk_candidate_candidatestatusdetailid foreign key (candidatestatusdetailid) references candidatestatusdetail (candidatestatusdetailid) on delete restrict on update restrict;

alter table candidatestatusdetail add constraint fk_candidatestatusdetail_reasonid foreign key (reasonid) references reason (reasonid) on delete restrict on update restrict;
create index ix_candidatestatusdetail_reasonid on candidatestatusdetail (reasonid);


# --- !Downs

alter table candidate drop foreign key fk_candidate_candidatestatusdetailid;

alter table candidate drop column candidatestatusdetailid;
alter table candidate drop column candidatesecondmobile;
alter table candidate drop column candidatethirdmobile;

alter table candidatestatusdetail drop foreign key fk_candidatestatusdetail_reasonid;
drop index ix_candidatestatusdetail_reasonid on candidatestatusdetail;

drop table if exists reason;

drop table if exists candidatestatusdetail;