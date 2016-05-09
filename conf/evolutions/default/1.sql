# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table auth (
  authid                        bigint signed not null auto_increment not null,
  candidateid                   bigint signed not null,
  passwordmd5                   char(60) not null,
  passwordsalt                  bigint signed not null,
  authsessionid                 varchar(50) not null not null,
  authsessionidexpirymillis     bigint signed not null not null,
  authcreatetimestamp           timestamp default current_timestamp not null,
  authupdatetimestamp           timestamp null,
  constraint pk_auth primary key (authid)
);

create table candidate (
  candidateid                   bigint signed not null auto_increment not null,
  candidateuuid                 varchar(255) not null not null,
  leadid                        bigint signed not null,
  candidatename                 varchar(50) not null,
  candidatelastname             varchar(50) not null,
  candidategender               int(1) null default 0,
  candidatedob                  varchar(20) null default 0,
  candidatemobile               varchar(13) not null,
  candidatephonetype            varchar(100) null,
  candidatemaritalstatus        int null default 0,
  candidateemail                varchar(50) not null,
  candidateisemployed           int not null,
  candidatetotalexperience      decimal(3,2) signed null default 0.00,
  candidatetype                 int signed not null default 0,
  candidatechannel              int signed not null default 0,
  candidateage                  int signed not null default 0,
  candidatecreatetimestamp      timestamp default current_timestamp not null,
  candidateupdatetimestamp      timestamp null,
  candidateotp                  int signed not null default 1234,
  candidateisassessed           int signed not null default 0,
  candidatesalaryslip           int signed not null default 0,
  candidateappointmentletter    int signed not null default 0,
  candidatemothertongue         int signed null,
  candidatehomelocality         bigint signed null,
  candidatestatusid             int signed null,
  educationid                   int signed null,
  constraint uq_candidate_candidateuuid unique (candidateuuid),
  constraint uq_candidate_leadid unique (leadid),
  constraint pk_candidate primary key (candidateid)
);

create table candidateprofilestatus (
  profilestatusid               int signed null auto_increment not null,
  profilestatusname             varchar(255) null,
  constraint pk_candidateprofilestatus primary key (profilestatusid)
);

create table candidatecurrentjobdetail (
  candidatecurrentjobid         bigint signed not null auto_increment not null,
  candidatecurrentcompany       bigint signed null,
  candidatecurrentjoblocation   bigint signed null,
  candidatetransportationmode   int null,
  candidatecurrentworkshift     int null,
  candidatecurrentdesignation   bigint signed null,
  candidatecurrentsalary        bigint signed null,
  candidatecurrentjobduration   int signed null,
  candidatecurrentemployerrefname varchar(100) null,
  candidatecurrentemployerrefmobile varchar(13) null,
  candidatecurrentjob           bigint signed null,
  updatetimestamp               timestamp default current_timestamp null,
  candidateid                   bigint signed not null,
  jobroleid                     bigint signed not null,
  constraint uq_candidatecurrentjobdetail_candidateid unique (candidateid),
  constraint pk_candidatecurrentjobdetail primary key (candidatecurrentjobid)
);

create table candidateskill (
  candidateskillid              int signed not null auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  candidateid                   bigint signed not null,
  skillid                       int signed not null,
  constraint pk_candidateskill primary key (candidateskillid)
);

create table channels (
  channelid                     int signed not null auto_increment not null,
  channelname                   varchar(50) not null default 0 not null,
  constraint pk_channels primary key (channelid)
);

create table developer (
  developerid                   bigint signed not null auto_increment not null,
  developername                 varchar(50) not null not null,
  developeraccesslevel          int not null not null,
  developerpasswordsalt         bigint signed not null not null,
  developerpasswordmd5          char(32) not null not null,
  developersessionid            varchar(50) not null not null,
  developersessionidexpirymillis bigint signed not null not null,
  developerapikey               varchar(255) not null not null,
  constraint uq_developer_developerapikey unique (developerapikey),
  constraint pk_developer primary key (developerid)
);

create table education (
  educationid                   int signed null auto_increment not null,
  educationname                 varchar(255) null,
  constraint pk_education primary key (educationid)
);

create table idproof (
  idproofid                     int signed not null auto_increment not null,
  idproofname                   varchar(255) null,
  constraint pk_idproof primary key (idproofid)
);

create table idproofreference (
  idproofreferenceid            int signed not null auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  candidateid                   bigint signed not null,
  idproofid                     int signed not null,
  constraint pk_idproofreference primary key (idproofreferenceid)
);

create table interaction (
  rowid                         int signed not null auto_increment not null,
  objectauuid                   varchar(255) not null not null,
  objectatype                   int signed not null not null,
  objectbuuid                   varchar(255) not null,
  objectbtype                   int signed null,
  interactiontype               int signed not null not null,
  note                          varchar(255) null,
  result                        varchar(255) null,
  creationtimestamp             timestamp default current_timestamp not null not null,
  createdby                     varchar(255) not null default 'system' not null,
  constraint pk_interaction primary key (rowid)
);

create table jobhistory (
  jobhistoryid                  bigint signed not null auto_increment not null,
  candidatepastcompany          bigint signed null,
  candidatepastsalary           bigint signed null,
  updatetimestamp               timestamp default current_timestamp null,
  jobroleid                     bigint signed not null,
  candidateid                   bigint signed not null,
  constraint pk_jobhistory primary key (jobhistoryid)
);

create table jobpreference (
  jobpreferenceid               int signed auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  jobroleid                     bigint signed not null,
  candidateid                   bigint signed not null,
  constraint pk_jobpreference primary key (jobpreferenceid)
);

create table jobrole (
  jobroleid                     bigint signed not null auto_increment not null,
  jobname                       varchar(255) null,
  constraint pk_jobrole primary key (jobroleid)
);

create table jobtoskill (
  jobtoskillid                  int signed not null auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  jobroleid                     bigint signed not null,
  skillid                       int signed not null,
  constraint pk_jobtoskill primary key (jobtoskillid)
);

create table language (
  languageid                    int signed null auto_increment not null,
  languagename                  varchar(255) null,
  constraint pk_language primary key (languageid)
);

create table languagepreference (
  languagepreference            int signed not null auto_increment not null,
  candidateid                   bigint signed not null,
  languageid                    int signed not null,
  verbalability                 int signed null,
  readingability                int signed null,
  writingability                int signed null,
  updatetimestamp               timestamp default current_timestamp null,
  constraint pk_languagepreference primary key (languagepreference)
);

create table lead (
  leadid                        bigint signed not null auto_increment not null,
  leaduuid                      varchar(255) not null not null,
  leadstatus                    int signed not null not null,
  leadname                      varchar(50) not null not null,
  leadmobile                    varchar(13) not null  not null,
  leadchannel                   int signed not null not null,
  leadtype                      int signed not null not null,
  leadinterest                  varchar(30),
  leadcreationtimestamp         timestamp default current_timestamp not null not null,
  constraint uq_lead_leaduuid unique (leaduuid),
  constraint pk_lead primary key (leadid)
);

create table leadtype (
  leadtypeid                    int signed not null auto_increment not null,
  leadtypename                  varchar(50) not null default 0 not null,
  constraint pk_leadtype primary key (leadtypeid)
);

create table locality (
  localityid                    bigint signed null auto_increment not null,
  localityname                  varchar(255) null,
  city                          varchar(255) null,
  state                         varchar(255) null,
  country                       varchar(255) null,
  latitude                      double(10,6) null,
  longitude                     double(10,6) null,
  constraint pk_locality primary key (localityid)
);

create table localitypreference (
  localitypreferenceid          bigint signed not null auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  localityid                    bigint signed null,
  candidateid                   bigint signed not null,
  constraint pk_localitypreference primary key (localitypreferenceid)
);

create table skill (
  skillid                       int signed not null auto_increment not null,
  skillname                     varchar(100) null,
  skilldescription              varchar(255) null,
  constraint pk_skill primary key (skillid)
);

create table timeshift (
  timeshiftid                   int signed auto_increment not null,
  timeshiftname                 varchar(50) not null,
  constraint pk_timeshift primary key (timeshiftid)
);

create table timeshiftpreference (
  timeshiftpreferenceid         int signed auto_increment not null,
  updatetimestamp               timestamp default current_timestamp null,
  candidateid                   bigint signed not null,
  timeshiftid                   int signed,
  constraint uq_timeshiftpreference_candidateid unique (candidateid),
  constraint uq_timeshiftpreference_timeshiftid unique (timeshiftid),
  constraint pk_timeshiftpreference primary key (timeshiftpreferenceid)
);

create table transportationmodes (
  transportationmodeid          int signed not null auto_increment not null,
  transportationmodename        varchar(255) null,
  constraint pk_transportationmodes primary key (transportationmodeid)
);

alter table candidate add constraint fk_candidate_candidatemothertongue foreign key (candidatemothertongue) references language (languageid) on delete restrict on update restrict;
create index ix_candidate_candidatemothertongue on candidate (candidatemothertongue);

alter table candidate add constraint fk_candidate_candidatehomelocality foreign key (candidatehomelocality) references locality (localityid) on delete restrict on update restrict;
create index ix_candidate_candidatehomelocality on candidate (candidatehomelocality);

alter table candidate add constraint fk_candidate_candidatestatusid foreign key (candidatestatusid) references candidateprofilestatus (profilestatusid) on delete restrict on update restrict;
create index ix_candidate_candidatestatusid on candidate (candidatestatusid);

alter table candidate add constraint fk_candidate_educationid foreign key (educationid) references education (educationid) on delete restrict on update restrict;
create index ix_candidate_educationid on candidate (educationid);

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_candidatecurrentjobdetail_jobroleid on candidatecurrentjobdetail (jobroleid);

alter table candidateskill add constraint fk_candidateskill_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidateskill_candidateid on candidateskill (candidateid);

alter table candidateskill add constraint fk_candidateskill_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_candidateskill_skillid on candidateskill (skillid);

alter table idproofreference add constraint fk_idproofreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_idproofreference_candidateid on idproofreference (candidateid);

alter table idproofreference add constraint fk_idproofreference_idproofid foreign key (idproofid) references idproof (idproofid) on delete restrict on update restrict;
create index ix_idproofreference_idproofid on idproofreference (idproofid);

alter table jobhistory add constraint fk_jobhistory_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_jobhistory_jobroleid on jobhistory (jobroleid);

alter table jobhistory add constraint fk_jobhistory_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_jobhistory_candidateid on jobhistory (candidateid);

alter table jobpreference add constraint fk_jobpreference_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_jobpreference_jobroleid on jobpreference (jobroleid);

alter table jobpreference add constraint fk_jobpreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_jobpreference_candidateid on jobpreference (candidateid);

alter table jobtoskill add constraint fk_jobtoskill_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_jobtoskill_jobroleid on jobtoskill (jobroleid);

alter table jobtoskill add constraint fk_jobtoskill_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_jobtoskill_skillid on jobtoskill (skillid);

alter table languagepreference add constraint fk_languagepreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_languagepreference_candidateid on languagepreference (candidateid);

alter table languagepreference add constraint fk_languagepreference_languageid foreign key (languageid) references language (languageid) on delete restrict on update restrict;
create index ix_languagepreference_languageid on languagepreference (languageid);

alter table localitypreference add constraint fk_localitypreference_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_localitypreference_localityid on localitypreference (localityid);

alter table localitypreference add constraint fk_localitypreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_localitypreference_candidateid on localitypreference (candidateid);

alter table timeshiftpreference add constraint fk_timeshiftpreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;

alter table timeshiftpreference add constraint fk_timeshiftpreference_timeshiftid foreign key (timeshiftid) references timeshift (timeshiftid) on delete restrict on update restrict;


# --- !Downs

alter table candidate drop foreign key fk_candidate_candidatemothertongue;
drop index ix_candidate_candidatemothertongue on candidate;

alter table candidate drop foreign key fk_candidate_candidatehomelocality;
drop index ix_candidate_candidatehomelocality on candidate;

alter table candidate drop foreign key fk_candidate_candidatestatusid;
drop index ix_candidate_candidatestatusid on candidate;

alter table candidate drop foreign key fk_candidate_educationid;
drop index ix_candidate_educationid on candidate;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_candidateid;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_jobroleid;
drop index ix_candidatecurrentjobdetail_jobroleid on candidatecurrentjobdetail;

alter table candidateskill drop foreign key fk_candidateskill_candidateid;
drop index ix_candidateskill_candidateid on candidateskill;

alter table candidateskill drop foreign key fk_candidateskill_skillid;
drop index ix_candidateskill_skillid on candidateskill;

alter table idproofreference drop foreign key fk_idproofreference_candidateid;
drop index ix_idproofreference_candidateid on idproofreference;

alter table idproofreference drop foreign key fk_idproofreference_idproofid;
drop index ix_idproofreference_idproofid on idproofreference;

alter table jobhistory drop foreign key fk_jobhistory_jobroleid;
drop index ix_jobhistory_jobroleid on jobhistory;

alter table jobhistory drop foreign key fk_jobhistory_candidateid;
drop index ix_jobhistory_candidateid on jobhistory;

alter table jobpreference drop foreign key fk_jobpreference_jobroleid;
drop index ix_jobpreference_jobroleid on jobpreference;

alter table jobpreference drop foreign key fk_jobpreference_candidateid;
drop index ix_jobpreference_candidateid on jobpreference;

alter table jobtoskill drop foreign key fk_jobtoskill_jobroleid;
drop index ix_jobtoskill_jobroleid on jobtoskill;

alter table jobtoskill drop foreign key fk_jobtoskill_skillid;
drop index ix_jobtoskill_skillid on jobtoskill;

alter table languagepreference drop foreign key fk_languagepreference_candidateid;
drop index ix_languagepreference_candidateid on languagepreference;

alter table languagepreference drop foreign key fk_languagepreference_languageid;
drop index ix_languagepreference_languageid on languagepreference;

alter table localitypreference drop foreign key fk_localitypreference_localityid;
drop index ix_localitypreference_localityid on localitypreference;

alter table localitypreference drop foreign key fk_localitypreference_candidateid;
drop index ix_localitypreference_candidateid on localitypreference;

alter table timeshiftpreference drop foreign key fk_timeshiftpreference_candidateid;

alter table timeshiftpreference drop foreign key fk_timeshiftpreference_timeshiftid;

drop table if exists auth;

drop table if exists candidate;

drop table if exists candidateprofilestatus;

drop table if exists candidatecurrentjobdetail;

drop table if exists candidateskill;

drop table if exists channels;

drop table if exists developer;

drop table if exists education;

drop table if exists idproof;

drop table if exists idproofreference;

drop table if exists interaction;

drop table if exists jobhistory;

drop table if exists jobpreference;

drop table if exists jobrole;

drop table if exists jobtoskill;

drop table if exists language;

drop table if exists languagepreference;

drop table if exists lead;

drop table if exists leadtype;

drop table if exists locality;

drop table if exists localitypreference;

drop table if exists skill;

drop table if exists timeshift;

drop table if exists timeshiftpreference;

drop table if exists transportationmodes;

