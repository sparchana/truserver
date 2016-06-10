# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table auth (
  authid                        bigint signed auto_increment not null,
  candidateid                   bigint signed not null,
  authstatus                    int signed not null not null,
  passwordmd5                   char(60) not null,
  passwordsalt                  bigint signed not null,
  authsessionid                 varchar(50) not null,
  authsessionidexpirymillis     bigint signed not null,
  authcreatetimestamp           timestamp not null,
  authupdatetimestamp           timestamp null,
  constraint pk_auth primary key (authid)
);

create table candidate (
  candidateid                   bigint signed auto_increment not null,
  candidateuuid                 varchar(255) not null not null,
  candidatename                 varchar(50) not null,
  candidatelastname             varchar(50) null,
  candidategender               int(1) null,
  candidatedob                  date null,
  candidatemobile               varchar(13) not null,
  candidatephonetype            varchar(100) null,
  candidatemaritalstatus        int null,
  candidateemail                varchar(255) null,
  candidateisemployed           int null,
  candidatetotalexperience      int signed null,
  candidateage                  int signed null,
  candidatecreatetimestamp      timestamp not null,
  candidateupdatetimestamp      timestamp null,
  candidateisassessed           int signed not null default 0,
  candidatesalaryslip           int signed null,
  candidateappointmentletter    int signed null,
  isminprofilecomplete          int signed not null default 0,
  candidatecurrentjobid         bigint signed,
  lead_leadid                   bigint signed,
  candidatemothertongue         int signed,
  candidatehomelocality         bigint signed,
  candidatestatusid             int signed,
  constraint uq_candidate_candidateuuid unique (candidateuuid),
  constraint uq_candidate_candidatecurrentjobid unique (candidatecurrentjobid),
  constraint uq_candidate_lead_leadid unique (lead_leadid),
  constraint pk_candidate primary key (candidateid)
);

create table candidateprofilestatus (
  profilestatusid               int signed auto_increment not null,
  profilestatusname             varchar(255) null,
  constraint pk_candidateprofilestatus primary key (profilestatusid)
);

create table candidatecurrentjobdetail (
  candidatecurrentjobid         bigint signed auto_increment not null,
  candidatecurrentcompany       varchar(100) null,
  candidatecurrentdesignation   varchar(255) null,
  candidatecurrentsalary        bigint signed null,
  candidatecurrentjobduration   int signed null,
  candidatecurrentemployerrefname varchar(100) null,
  candidatecurrentemployerrefmobile varchar(13) null,
  updatetimestamp               timestamp null,
  jobroleid                     bigint signed,
  localityid                    bigint signed,
  transportationmodeid          int signed,
  timeshiftid                   int signed,
  constraint pk_candidatecurrentjobdetail primary key (candidatecurrentjobid)
);

create table candidateeducation (
  candidateeducationid          int signed auto_increment not null,
  updatetimestamp               timestamp null,
  candidatelastinstitute        varchar(256) null,
  candidateid                   bigint signed,
  educationid                   int signed,
  degreeid                      int signed,
  constraint uq_candidateeducation_candidateid unique (candidateid),
  constraint pk_candidateeducation primary key (candidateeducationid)
);

create table candidateskill (
  candidateskillid              int signed not null auto_increment not null,
  updatetimestamp               timestamp null,
  candidateid                   bigint signed,
  skillid                       int signed,
  skillqualifierid              int signed,
  constraint pk_candidateskill primary key (candidateskillid)
);

create table channels (
  channelid                     int signed auto_increment not null,
  channelname                   varchar(50) not null default 0 not null,
  constraint pk_channels primary key (channelid)
);

create table degree (
  degreeid                      int signed auto_increment not null,
  degreename                    varchar(100) null,
  constraint pk_degree primary key (degreeid)
);

create table developer (
  developerid                   bigint signed not null auto_increment not null,
  developername                 varchar(50) not null not null,
  developeraccesslevel          int not null not null,
  developerpasswordsalt         bigint signed not null not null,
  developerpasswordmd5          char(32) not null not null,
  developersessionid            varchar(50) null,
  developersessionidexpirymillis bigint signed,
  developerapikey               varchar(255) not null not null,
  constraint uq_developer_developerapikey unique (developerapikey),
  constraint pk_developer primary key (developerid)
);

create table education (
  educationid                   int signed auto_increment not null,
  educationname                 varchar(255) null,
  constraint pk_education primary key (educationid)
);

create table idproof (
  idproofid                     int signed auto_increment not null,
  idproofname                   varchar(255) null,
  constraint pk_idproof primary key (idproofid)
);

create table idproofreference (
  idproofreferenceid            int signed auto_increment not null,
  updatetimestamp               timestamp null,
  candidateid                   bigint signed,
  idproofid                     int signed,
  constraint pk_idproofreference primary key (idproofreferenceid)
);

create table interaction (
  rowid                         int signed auto_increment not null,
  objectauuid                   varchar(255) not null not null,
  objectatype                   int signed not null not null,
  objectbuuid                   varchar(255) not null,
  objectbtype                   int signed null,
  interactiontype               int signed null,
  note                          varchar(255) null,
  result                        varchar(255) null,
  creationtimestamp             timestamp default current_timestamp not null not null,
  createdby                     varchar(255) not null default 'System' not null,
  constraint pk_interaction primary key (rowid)
);

create table jobhistory (
  jobhistoryid                  bigint signed auto_increment not null,
  candidatepastcompany          varchar(255) null,
  candidatepastsalary           bigint signed null,
  updatetimestamp               timestamp null,
  jobroleid                     bigint signed,
  candidateid                   bigint signed,
  constraint pk_jobhistory primary key (jobhistoryid)
);

create table jobpreference (
  jobpreferenceid               int signed auto_increment not null,
  updatetimestamp               timestamp null,
  jobroleid                     bigint signed,
  candidateid                   bigint signed,
  constraint pk_jobpreference primary key (jobpreferenceid)
);

create table jobrole (
  jobroleid                     bigint signed auto_increment not null,
  jobname                       varchar(255) null,
  constraint pk_jobrole primary key (jobroleid)
);

create table jobtoskill (
  jobtoskillid                  int signed auto_increment not null,
  updatetimestamp               timestamp null,
  jobroleid                     bigint signed,
  skillid                       int signed,
  constraint pk_jobtoskill primary key (jobtoskillid)
);

create table language (
  languageid                    int signed auto_increment not null,
  languagename                  varchar(255) null,
  constraint pk_language primary key (languageid)
);

create table languageknown (
  languageknownid               int signed auto_increment not null,
  verbalability                 int signed null,
  readingability                int signed null,
  writingability                int signed null,
  updatetimestamp               timestamp null,
  languageid                    int signed,
  candidateid                   bigint signed,
  constraint pk_languageknown primary key (languageknownid)
);

create table lead (
  leadid                        bigint signed auto_increment not null,
  leaduuid                      varchar(255) not null not null,
  leadstatus                    int signed not null not null,
  leadname                      varchar(50) not null not null,
  leadmobile                    varchar(13) not null  not null,
  leadchannel                   int signed not null not null,
  leadtype                      int signed not null not null,
  leadinterest                  varchar(30),
  leadcreationtimestamp         timestamp not null not null,
  leadsourceid                  int signed,
  constraint uq_lead_leaduuid unique (leaduuid),
  constraint pk_lead primary key (leadid)
);

create table leadsource (
  leadsourceid                  int signed auto_increment not null,
  leadsourcename                varchar(255) null,
  constraint pk_leadsource primary key (leadsourceid)
);

create table locality (
  localityid                    bigint signed auto_increment not null,
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
  updatetimestamp               timestamp null,
  localityid                    bigint signed,
  candidateid                   bigint signed,
  constraint pk_localitypreference primary key (localitypreferenceid)
);

create table skill (
  skillid                       int signed auto_increment not null,
  skillname                     varchar(100) null,
  skillquestion                 varchar(255) null,
  constraint pk_skill primary key (skillid)
);

create table skillqualifier (
  skillqualifierid              int signed auto_increment not null,
  qualifier                     varchar(100) null,
  skillid                       int signed,
  constraint pk_skillqualifier primary key (skillqualifierid)
);

create table timeshift (
  timeshiftid                   int signed auto_increment not null,
  timeshiftname                 varchar(50) null,
  constraint pk_timeshift primary key (timeshiftid)
);

create table timeshiftpreference (
  timeshiftpreferenceid         int signed auto_increment not null,
  updatetimestamp               timestamp null,
  candidateid                   bigint signed,
  timeshiftid                   int signed,
  constraint uq_timeshiftpreference_candidateid unique (candidateid),
  constraint pk_timeshiftpreference primary key (timeshiftpreferenceid)
);

create table transportationmodes (
  transportationmodeid          int signed auto_increment not null,
  transportationmodename        varchar(255) null,
  constraint pk_transportationmodes primary key (transportationmodeid)
);

alter table candidate add constraint fk_candidate_candidatecurrentjobid foreign key (candidatecurrentjobid) references candidatecurrentjobdetail (candidatecurrentjobid) on delete restrict on update restrict;

alter table candidate add constraint fk_candidate_lead_leadid foreign key (lead_leadid) references lead (leadid) on delete restrict on update restrict;

alter table candidate add constraint fk_candidate_candidatemothertongue foreign key (candidatemothertongue) references language (languageid) on delete restrict on update restrict;
create index ix_candidate_candidatemothertongue on candidate (candidatemothertongue);

alter table candidate add constraint fk_candidate_candidatehomelocality foreign key (candidatehomelocality) references locality (localityid) on delete restrict on update restrict;
create index ix_candidate_candidatehomelocality on candidate (candidatehomelocality);

alter table candidate add constraint fk_candidate_candidatestatusid foreign key (candidatestatusid) references candidateprofilestatus (profilestatusid) on delete restrict on update restrict;
create index ix_candidate_candidatestatusid on candidate (candidatestatusid);

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_candidatecurrentjobdetail_jobroleid on candidatecurrentjobdetail (jobroleid);

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_candidatecurrentjobdetail_localityid on candidatecurrentjobdetail (localityid);

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_transportationmodeid foreign key (transportationmodeid) references transportationmodes (transportationmodeid) on delete restrict on update restrict;
create index ix_candidatecurrentjobdetail_transportationmodeid on candidatecurrentjobdetail (transportationmodeid);

alter table candidatecurrentjobdetail add constraint fk_candidatecurrentjobdetail_timeshiftid foreign key (timeshiftid) references timeshift (timeshiftid) on delete restrict on update restrict;
create index ix_candidatecurrentjobdetail_timeshiftid on candidatecurrentjobdetail (timeshiftid);

alter table candidateeducation add constraint fk_candidateeducation_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;

alter table candidateeducation add constraint fk_candidateeducation_educationid foreign key (educationid) references education (educationid) on delete restrict on update restrict;
create index ix_candidateeducation_educationid on candidateeducation (educationid);

alter table candidateeducation add constraint fk_candidateeducation_degreeid foreign key (degreeid) references degree (degreeid) on delete restrict on update restrict;
create index ix_candidateeducation_degreeid on candidateeducation (degreeid);

alter table candidateskill add constraint fk_candidateskill_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidateskill_candidateid on candidateskill (candidateid);

alter table candidateskill add constraint fk_candidateskill_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_candidateskill_skillid on candidateskill (skillid);

alter table candidateskill add constraint fk_candidateskill_skillqualifierid foreign key (skillqualifierid) references skillqualifier (skillqualifierid) on delete restrict on update restrict;
create index ix_candidateskill_skillqualifierid on candidateskill (skillqualifierid);

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

alter table languageknown add constraint fk_languageknown_languageid foreign key (languageid) references language (languageid) on delete restrict on update restrict;
create index ix_languageknown_languageid on languageknown (languageid);

alter table languageknown add constraint fk_languageknown_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_languageknown_candidateid on languageknown (candidateid);

alter table lead add constraint fk_lead_leadsourceid foreign key (leadsourceid) references leadsource (leadsourceid) on delete restrict on update restrict;
create index ix_lead_leadsourceid on lead (leadsourceid);

alter table localitypreference add constraint fk_localitypreference_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_localitypreference_localityid on localitypreference (localityid);

alter table localitypreference add constraint fk_localitypreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_localitypreference_candidateid on localitypreference (candidateid);

alter table skillqualifier add constraint fk_skillqualifier_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_skillqualifier_skillid on skillqualifier (skillid);

alter table timeshiftpreference add constraint fk_timeshiftpreference_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;

alter table timeshiftpreference add constraint fk_timeshiftpreference_timeshiftid foreign key (timeshiftid) references timeshift (timeshiftid) on delete restrict on update restrict;
create index ix_timeshiftpreference_timeshiftid on timeshiftpreference (timeshiftid);


# --- !Downs

alter table candidate drop foreign key fk_candidate_candidatecurrentjobid;

alter table candidate drop foreign key fk_candidate_lead_leadid;

alter table candidate drop foreign key fk_candidate_candidatemothertongue;
drop index ix_candidate_candidatemothertongue on candidate;

alter table candidate drop foreign key fk_candidate_candidatehomelocality;
drop index ix_candidate_candidatehomelocality on candidate;

alter table candidate drop foreign key fk_candidate_candidatestatusid;
drop index ix_candidate_candidatestatusid on candidate;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_jobroleid;
drop index ix_candidatecurrentjobdetail_jobroleid on candidatecurrentjobdetail;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_localityid;
drop index ix_candidatecurrentjobdetail_localityid on candidatecurrentjobdetail;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_transportationmodeid;
drop index ix_candidatecurrentjobdetail_transportationmodeid on candidatecurrentjobdetail;

alter table candidatecurrentjobdetail drop foreign key fk_candidatecurrentjobdetail_timeshiftid;
drop index ix_candidatecurrentjobdetail_timeshiftid on candidatecurrentjobdetail;

alter table candidateeducation drop foreign key fk_candidateeducation_candidateid;

alter table candidateeducation drop foreign key fk_candidateeducation_educationid;
drop index ix_candidateeducation_educationid on candidateeducation;

alter table candidateeducation drop foreign key fk_candidateeducation_degreeid;
drop index ix_candidateeducation_degreeid on candidateeducation;

alter table candidateskill drop foreign key fk_candidateskill_candidateid;
drop index ix_candidateskill_candidateid on candidateskill;

alter table candidateskill drop foreign key fk_candidateskill_skillid;
drop index ix_candidateskill_skillid on candidateskill;

alter table candidateskill drop foreign key fk_candidateskill_skillqualifierid;
drop index ix_candidateskill_skillqualifierid on candidateskill;

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

alter table languageknown drop foreign key fk_languageknown_languageid;
drop index ix_languageknown_languageid on languageknown;

alter table languageknown drop foreign key fk_languageknown_candidateid;
drop index ix_languageknown_candidateid on languageknown;

alter table lead drop foreign key fk_lead_leadsourceid;
drop index ix_lead_leadsourceid on lead;

alter table localitypreference drop foreign key fk_localitypreference_localityid;
drop index ix_localitypreference_localityid on localitypreference;

alter table localitypreference drop foreign key fk_localitypreference_candidateid;
drop index ix_localitypreference_candidateid on localitypreference;

alter table skillqualifier drop foreign key fk_skillqualifier_skillid;
drop index ix_skillqualifier_skillid on skillqualifier;

alter table timeshiftpreference drop foreign key fk_timeshiftpreference_candidateid;

alter table timeshiftpreference drop foreign key fk_timeshiftpreference_timeshiftid;
drop index ix_timeshiftpreference_timeshiftid on timeshiftpreference;

drop table if exists auth;

drop table if exists candidate;

drop table if exists candidateprofilestatus;

drop table if exists candidatecurrentjobdetail;

drop table if exists candidateeducation;

drop table if exists candidateskill;

drop table if exists channels;

drop table if exists degree;

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

drop table if exists languageknown;

drop table if exists lead;

drop table if exists leadsource;

drop table if exists locality;

drop table if exists localitypreference;

drop table if exists skill;

drop table if exists skillqualifier;

drop table if exists timeshift;

drop table if exists timeshiftpreference;

drop table if exists transportationmodes;

