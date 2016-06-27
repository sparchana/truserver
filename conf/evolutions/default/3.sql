# --- !Ups

create table company (
  companyid                     bigint signed auto_increment not null,
  companyuuid                   varchar(255) not null,
  companyname                   varchar(50) not null,
  companyemployeecount          varchar(15) null,
  companywebsite                varchar(30) null,
  companydescription            varchar(5000) null,
  companyaddress                varchar(1000) null,
  latitude                      double(10,6) null,
  longitude                     double(10,6) null,
  companypincode                bigint signed null,
  companylogo                   varchar(80) null,
  companycreatetimestamp        timestamp not null,
  companyupdatetimestamp        timestamp not null,
  companylocality               bigint signed null,
  comptype                      bigint signed null,
  compstatus                    bigint signed null,
  constraint pk_company primary key (companyid)
);

create table companystatus (
  companystatusid               bigint signed auto_increment not null,
  companystatusname             varchar(20) not null,
  constraint pk_companystatus primary key (companystatusid)
);

create table companytype (
  companytypeid                 bigint signed auto_increment not null,
  companytypename               varchar(100) not null,
  constraint pk_companytype primary key (companytypeid)
);

create table experience (
  experienceid                  bigint signed auto_increment not null,
  experiencetype                varchar(20) null,
  constraint pk_experience primary key (experienceid)
);

create table jobapplication (
  jobapplicationid              int signed auto_increment not null,
  jobapplicationcreatetimestamp timestamp not null,
  screeningcomments             varchar(1000) null,
  jobpostid                     bigint signed null,
  screeningstatusid             bigint signed null,
  candidateid                   bigint signed null,
  jobapplicationupdatetimestamp timestamp null,
  constraint pk_jobapplication primary key (jobapplicationid)
);

create table jobbenefit (
  jobbenefitid                  bigint signed auto_increment not null,
  jobbenefitname                varchar(20) not null,
  constraint pk_jobbenefit primary key (jobbenefitid)
);

create table jobpost (
  jobpostid                     bigint signed auto_increment not null,
  jobpostuuid                   varchar(255) not null,
  jobpostcreatetimestamp        timestamp not null,
  jobpostminsalary              bigint signed null,
  jobpostmaxsalary              bigint signed null,
  jobpoststarttime              int signed null,
  jobpostendtime                int signed null,
  jobpostishot                  int signed null,
  jobpostdescription            varchar(5000) null,
  jobposttitle                  varchar(100) null,
  jobpostincentives             varchar(5000) null,
  jobpostminrequirement         varchar(5000) null,
  jobpostaddress                varchar(1000) null,
  latitude                      double(10,6) null,
  longitude                     double(10,6) null,
  jobpostpincode                bigint signed null,
  jobpostvacancies              bigint signed null,
  jobpostdescriptionaudio       varchar(100) null,
  jobpostworkfromhome           int signed null,
  jobstatus                     bigint signed null,
  pricingplantype               bigint signed null,
  jobpostjobrole                bigint signed null,
  companyid                     bigint signed null,
  jobshiftid                    int signed null,
  jobexperienceid               bigint signed null,
  jobeducationid                int signed null,
  jobpostupdatetimestamp        timestamp null,
  constraint pk_jobpost primary key (jobpostid)
);

create table jobposttobenefits (
  jobposttobenefitsid           bigint signed not null auto_increment not null,
  jobposttobenefitscreatetimestamp timestamp null,
  jobbenefitid                  bigint signed null,
  jobpostid                     bigint signed null,
  jobposttobenefitsupdatetimestamp timestamp null not null,
  constraint pk_jobposttobenefits primary key (jobposttobenefitsid)
);

create table jobposttolocality (
  jobposttolocalityid           bigint signed not null auto_increment not null,
  jobposttolocalitycreatetimestamp timestamp null,
  localityid                    bigint signed null,
  jobpostid                     bigint signed null,
  jobposttolocalityupdatetimestamp timestamp null not null,
  constraint pk_jobposttolocality primary key (jobposttolocalityid)
);

create table jobposttoskill (
  jobposttoskillid              bigint signed not null auto_increment not null,
  jobposttoskillcreatetimestamp timestamp not null,
  jobpostid                     bigint signed null,
  skillid                       int signed,
  jobposttoskillupdatetimestamp timestamp null not null,
  constraint pk_jobposttoskill primary key (jobposttoskillid)
);

create table jobstatus (
  jobstatusid                   bigint signed auto_increment not null,
  jobstatusname                 varchar(20) not null,
  constraint pk_jobstatus primary key (jobstatusid)
);

create table pricingplantype (
  pricingplantypeid             bigint signed auto_increment not null,
  pricingplantypename           varchar(50) not null,
  constraint pk_pricingplantype primary key (pricingplantypeid)
);

create table recruiterprofile (
  recruiterprofileid            bigint signed auto_increment not null,
  recruiterprofileuuid          varchar(255) not null,
  recruiterprofilename          varchar(50) not null,
  recruiterprofilemobile        varchar(13) not null,
  recruiterprofilelandline      varchar(13) not null,
  recruiterprofilepin           int signed null,
  recruiterprofileemail         varchar(255) null,
  recruiterprofilecreatetimestamp timestamp not null,
  recruiterprofileupdatetimestamp timestamp not null,
  recstatus                     bigint signed null,
  constraint pk_recruiterprofile primary key (recruiterprofileid)
);

create table recruiterstatus (
  recruiterstatusid             bigint signed auto_increment not null,
  recruiterstatusname           varchar(20) not null,
  constraint pk_recruiterstatus primary key (recruiterstatusid)
);

create table screeningstatus (
  screeningstatusid             bigint signed auto_increment not null,
  screeningstatusname           varchar(20) not null,
  constraint pk_screeningstatus primary key (screeningstatusid)
);


alter table company add constraint fk_company_companylocality foreign key (companylocality) references locality (localityid) on delete restrict on update restrict;
create index ix_company_companylocality on company (companylocality);

alter table company add constraint fk_company_comptype foreign key (comptype) references companytype (companytypeid) on delete restrict on update restrict;
create index ix_company_comptype on company (comptype);

alter table company add constraint fk_company_compstatus foreign key (compstatus) references companystatus (companystatusid) on delete restrict on update restrict;
create index ix_company_compstatus on company (compstatus);

alter table jobapplication add constraint fk_jobapplication_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobapplication_jobpostid on jobapplication (jobpostid);

alter table jobapplication add constraint fk_jobapplication_screeningstatusid foreign key (screeningstatusid) references screeningstatus (screeningstatusid) on delete restrict on update restrict;
create index ix_jobapplication_screeningstatusid on jobapplication (screeningstatusid);

alter table jobapplication add constraint fk_jobapplication_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_jobapplication_candidateid on jobapplication (candidateid);

alter table jobpost add constraint fk_jobpost_jobstatus foreign key (jobstatus) references jobstatus (jobstatusid) on delete restrict on update restrict;
create index ix_jobpost_jobstatus on jobpost (jobstatus);

alter table jobpost add constraint fk_jobpost_pricingplantype foreign key (pricingplantype) references pricingplantype (pricingplantypeid) on delete restrict on update restrict;
create index ix_jobpost_pricingplantype on jobpost (pricingplantype);

alter table jobpost add constraint fk_jobpost_jobpostjobrole foreign key (jobpostjobrole) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_jobpost_jobpostjobrole on jobpost (jobpostjobrole);

alter table jobpost add constraint fk_jobpost_companyid foreign key (companyid) references company (companyid) on delete restrict on update restrict;
create index ix_jobpost_companyid on jobpost (companyid);

alter table jobpost add constraint fk_jobpost_jobshiftid foreign key (jobshiftid) references timeshift (timeshiftid) on delete restrict on update restrict;
create index ix_jobpost_jobshiftid on jobpost (jobshiftid);

alter table jobpost add constraint fk_jobpost_jobexperienceid foreign key (jobexperienceid) references experience (experienceid) on delete restrict on update restrict;
create index ix_jobpost_jobexperienceid on jobpost (jobexperienceid);

alter table jobpost add constraint fk_jobpost_jobeducationid foreign key (jobeducationid) references education (educationid) on delete restrict on update restrict;
create index ix_jobpost_jobeducationid on jobpost (jobeducationid);

alter table jobposttobenefits add constraint fk_jobposttobenefits_jobbenefitid foreign key (jobbenefitid) references jobbenefit (jobbenefitid) on delete restrict on update restrict;
create index ix_jobposttobenefits_jobbenefitid on jobposttobenefits (jobbenefitid);

alter table jobposttobenefits add constraint fk_jobposttobenefits_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobposttobenefits_jobpostid on jobposttobenefits (jobpostid);

alter table jobposttolocality add constraint fk_jobposttolocality_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_jobposttolocality_localityid on jobposttolocality (localityid);

alter table jobposttolocality add constraint fk_jobposttolocality_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobposttolocality_jobpostid on jobposttolocality (jobpostid);

alter table jobposttoskill add constraint fk_jobposttoskill_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobposttoskill_jobpostid on jobposttoskill (jobpostid);

alter table jobposttoskill add constraint fk_jobposttoskill_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_jobposttoskill_skillid on jobposttoskill (skillid);

alter table recruiterprofile add constraint fk_recruiterprofile_recstatus foreign key (recstatus) references recruiterstatus (recruiterstatusid) on delete restrict on update restrict;
create index ix_recruiterprofile_recstatus on recruiterprofile (recstatus);

# --- !Downs

alter table company drop foreign key fk_company_companylocality;
drop index ix_company_companylocality on company;

alter table company drop foreign key fk_company_comptype;
drop index ix_company_comptype on company;

alter table company drop foreign key fk_company_compstatus;
drop index ix_company_compstatus on company;

alter table jobapplication drop foreign key fk_jobapplication_jobpostid;
drop index ix_jobapplication_jobpostid on jobapplication;

alter table jobapplication drop foreign key fk_jobapplication_screeningstatusid;
drop index ix_jobapplication_screeningstatusid on jobapplication;

alter table jobapplication drop foreign key fk_jobapplication_candidateid;
drop index ix_jobapplication_candidateid on jobapplication;

alter table jobpost drop foreign key fk_jobpost_jobstatus;
drop index ix_jobpost_jobstatus on jobpost;

alter table jobpost drop foreign key fk_jobpost_pricingplantype;
drop index ix_jobpost_pricingplantype on jobpost;

alter table jobpost drop foreign key fk_jobpost_jobpostjobrole;
drop index ix_jobpost_jobpostjobrole on jobpost;

alter table jobpost drop foreign key fk_jobpost_companyid;
drop index ix_jobpost_companyid on jobpost;

alter table jobpost drop foreign key fk_jobpost_jobshiftid;
drop index ix_jobpost_jobshiftid on jobpost;

alter table jobpost drop foreign key fk_jobpost_jobexperienceid;
drop index ix_jobpost_jobexperienceid on jobpost;

alter table jobpost drop foreign key fk_jobpost_jobeducationid;
drop index ix_jobpost_jobeducationid on jobpost;

alter table jobposttobenefits drop foreign key fk_jobposttobenefits_jobbenefitid;
drop index ix_jobposttobenefits_jobbenefitid on jobposttobenefits;

alter table jobposttobenefits drop foreign key fk_jobposttobenefits_jobpostid;
drop index ix_jobposttobenefits_jobpostid on jobposttobenefits;

alter table jobposttolocality drop foreign key fk_jobposttolocality_localityid;
drop index ix_jobposttolocality_localityid on jobposttolocality;

alter table jobposttolocality drop foreign key fk_jobposttolocality_jobpostid;
drop index ix_jobposttolocality_jobpostid on jobposttolocality;

alter table jobposttoskill drop foreign key fk_jobposttoskill_jobpostid;
drop index ix_jobposttoskill_jobpostid on jobposttoskill;

alter table jobposttoskill drop foreign key fk_jobposttoskill_skillid;
drop index ix_jobposttoskill_skillid on jobposttoskill;

alter table recruiterprofile drop foreign key fk_recruiterprofile_recstatus;
drop index ix_recruiterprofile_recstatus on recruiterprofile;

drop table if exists company;

drop table if exists companystatus;

drop table if exists companytype;

drop table if exists experience;

drop table if exists jobapplication;

drop table if exists jobbenefit;

drop table if exists jobpost;

drop table if exists jobposttobenefits;

drop table if exists jobposttolocality;

drop table if exists jobposttoskill;

drop table if exists jobstatus;

drop table if exists pricingplantype;

drop table if exists recruiterprofile;

drop table if exists recruiterstatus;

drop table if exists screeningstatus;