
# --- !Ups

alter table candidateskill add column candidateskillresponse  bit null;


create table expcategory (
  expcategoryid                 int signed auto_increment not null,
  expcategoryname               text null,
  constraint pk_expcategory primary key (expcategoryid)
);


create table jobexpquestion (
  jobexpquestionid              int signed auto_increment not null,
  jobroleid                     bigint signed,
  expcategoryid                 int signed,
  jobexpquestion                text null,
  constraint pk_jobexpquestion primary key (jobexpquestionid)
);

create table jobexpresponse (
  jobexpresponseid              int signed auto_increment not null,
  jobexpquestionid              int signed,
  jobexpresponseoptionid        int signed,
  constraint pk_jobexpresponse primary key (jobexpresponseid)
);

create table jobexpresponseoption (
  jobexpresponseoptionid        int signed auto_increment not null,
  responsegroupid               int signed null,
  jobexpresponsenameid          varchar(255),
  constraint uq_jobexpresponseoption_jobexpresponsenameid unique (jobexpresponsenameid),
  constraint pk_jobexpresponseoption primary key (jobexpresponseoptionid)
);


create table candidateexp (
  candidateexpid                int signed not null auto_increment not null,
  candidateid                   bigint signed,
  jobexpquestionid              int signed,
  jobexpresponseid              int signed,
  updatetimestamp               timestamp null not null,
  constraint pk_candidateexp primary key (candidateexpid)
);


alter table jobexpquestion add constraint fk_jobexpquestion_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_jobexpquestion_jobroleid on jobexpquestion (jobroleid);

alter table jobexpquestion add constraint fk_jobexpquestion_expcategoryid foreign key (expcategoryid) references expcategory (expcategoryid) on delete restrict on update restrict;
create index ix_jobexpquestion_expcategoryid on jobexpquestion (expcategoryid);

alter table jobexpresponse add constraint fk_jobexpresponse_jobexpquestionid foreign key (jobexpquestionid) references jobexpquestion (jobexpquestionid) on delete restrict on update restrict;
create index ix_jobexpresponse_jobexpquestionid on jobexpresponse (jobexpquestionid);

alter table jobexpresponse add constraint fk_jobexpresponse_jobexpresponseoptionid foreign key (jobexpresponseoptionid) references jobexpresponseoption (jobexpresponseoptionid) on delete restrict on update restrict;
create index ix_jobexpresponse_jobexpresponseoptionid on jobexpresponse (jobexpresponseoptionid);


alter table candidateexp add constraint fk_candidateexp_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidateexp_candidateid on candidateexp (candidateid);

alter table candidateexp add constraint fk_candidateexp_jobexpquestionid foreign key (jobexpquestionid) references jobexpquestion (jobexpquestionid) on delete restrict on update restrict;
create index ix_candidateexp_jobexpquestionid on candidateexp (jobexpquestionid);

alter table candidateexp add constraint fk_candidateexp_jobexpresponseid foreign key (jobexpresponseid) references jobexpresponse (jobexpresponseid) on delete restrict on update restrict;
create index ix_candidateexp_jobexpresponseid on candidateexp (jobexpresponseid);


# --- !Downs
 
alter table jobexpquestion drop foreign key fk_jobexpquestion_jobroleid;
drop index ix_jobexpquestion_jobroleid on jobexpquestion;

alter table jobexpquestion drop foreign key fk_jobexpquestion_expcategoryid;
drop index ix_jobexpquestion_expcategoryid on jobexpquestion;

alter table jobexpresponse drop foreign key fk_jobexpresponse_jobexpquestionid;
drop index ix_jobexpresponse_jobexpquestionid on jobexpresponse;

alter table jobexpresponse drop foreign key fk_jobexpresponse_jobexpresponseoptionid;
drop index ix_jobexpresponse_jobexpresponseoptionid on jobexpresponse;

alter table candidateexp drop foreign key fk_candidateexp_candidateid;
drop index ix_candidateexp_candidateid on candidateexp;

alter table candidateexp drop foreign key fk_candidateexp_jobexpquestionid;
drop index ix_candidateexp_jobexpquestionid on candidateexp;

alter table candidateexp drop foreign key fk_candidateexp_jobexpresponseid;
drop index ix_candidateexp_jobexpresponseid on candidateexp;


alter table candidateskill drop column candidateskillresponse;

drop table if exists expcategory;

drop table if exists jobexpquestion;

drop table if exists jobexpresponse;

drop table if exists jobexpresponseoption;

drop table if exists candidateexp;
