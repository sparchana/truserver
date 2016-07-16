# --- !Ups
alter table candidate add column candidatestatusdetailid int null;
alter table candidate add column candidatesecondmobile varchar(13) null;
alter table candidate add column candidatethirdmobile varchar(13) null;

create table candidatestatusdetail (
  candidatestatusdetailid       int signed auto_increment not null,
  reason                        text null,
  duration                      int signed null,
  createtimestamp               timestamp not null default current_timestamp,
  updatetimestamp               timestamp null,
  statusexpirydate              date null,
  constraint pk_candidatestatusdetail primary key (candidatestatusdetailid)
);

alter table candidate add constraint fk_candidate_candidatestatusdetailid foreign key (candidatestatusdetailid) references candidatestatusdetail (candidatestatusdetailid) on delete restrict on update restrict;


# --- !Downs

alter table candidate drop foreign key fk_candidate_candidatestatusdetailid;

alter table candidate drop column candidatestatusdetailid;
alter table candidate drop column candidatesecondmobile;
alter table candidate drop column candidatethirdmobile;

drop table if exists candidatestatusdetail;