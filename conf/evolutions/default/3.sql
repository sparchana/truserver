# --- !Ups

create table company (
	companyid                     bigint signed auto_increment not null,
	companyuuid                   varchar(255) not null,
	companyname                   varchar(50) not null,
	companyemployeecount          int signed null,
	companywebsite                varchar(30) null,
	companydescription            varchar(500) null,
	companylogo                   varchar(80) null,
	companycreatetimestamp        timestamp null,
	companyupdatetimestamp        timestamp null,
	companylocality               bigint signed,
	comptype                      bigint signed,
	compstatus                    bigint signed,
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
	jobapplicationcreatetimestamp timestamp null,
	jobpostid                     bigint signed,
	candidateid                   bigint signed,
	constraint pk_jobapplication primary key (jobapplicationid)
);

create table jobpost (
	jobpostid                     bigint signed auto_increment not null,
	jobpostuuid                   varchar(255) not null,
	jobpostcreatetimestamp        timestamp not null,
	jobpostupdatetimestamp        timestamp null,
	jobpostminsalary              bigint signed null,
	jobpostmaxsalary              bigint signed null,
	jobpoststarttime              time null,
	jobpostendtime                time null,
	jobpostbenefitpf              int signed null,
	jobpostbenefitfuel            int signed null,
	jobpostbenefitinsurance       int signed null,
	jobpostdescription            varchar(1000) null,
	jobposttitle                  varchar(100) null,
	jobpostvacancy                bigint signed null,
	jobpostdescriptionaudio       varchar(100) null,
	jobpostworkfromhome           int signed null,
	jobpostworkingdays            binary(7) null,
	jobstatus                     bigint signed,
	jobpostjobrole                bigint signed,
	companyid                     bigint signed,
	jobshiftid                    int signed,
	jobexperienceid               bigint signed,
	jobeducationid                int signed,
	constraint pk_jobpost primary key (jobpostid)
);

create table jobposttolocality (
	jobposttolocalityid           bigint signed not null auto_increment not null,
	jobposttolocalityupdatetimestamp timestamp null,
	localityid                    bigint signed,
	jobpostid                     bigint signed,
	constraint pk_jobposttolocality primary key (jobposttolocalityid)
);

create table jobposttoskill (
	jobposttoskillid              bigint signed not null auto_increment not null,
	jobposttoskillupdatetimestamp timestamp null,
	jobpostid                     bigint signed,
	skillid                       int signed,
	constraint pk_jobposttoskill primary key (jobposttoskillid)
);

create table jobstatus (
	jobstatusid                   bigint signed auto_increment not null,
	jobstatusname                 varchar(20) not null,
	constraint pk_jobstatus primary key (jobstatusid)
);

alter table company add constraint fk_company_companylocality foreign key (companylocality) references locality (localityid) on delete restrict on update restrict;
create index ix_company_companylocality on company (companylocality);

alter table company add constraint fk_company_comptype foreign key (comptype) references companytype (companytypeid) on delete restrict on update restrict;
create index ix_company_comptype on company (comptype);

alter table company add constraint fk_company_compstatus foreign key (compstatus) references companystatus (companystatusid) on delete restrict on update restrict;
create index ix_company_compstatus on company (compstatus);

alter table jobapplication add constraint fk_jobapplication_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobapplication_jobpostid on jobapplication (jobpostid);

alter table jobapplication add constraint fk_jobapplication_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_jobapplication_candidateid on jobapplication (candidateid);

alter table jobpost add constraint fk_jobpost_jobstatus foreign key (jobstatus) references jobstatus (jobstatusid) on delete restrict on update restrict;
create index ix_jobpost_jobstatus on jobpost (jobstatus);

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

alter table jobposttolocality add constraint fk_jobposttolocality_localityid foreign key (localityid) references locality (localityid) on delete restrict on update restrict;
create index ix_jobposttolocality_localityid on jobposttolocality (localityid);

alter table jobposttolocality add constraint fk_jobposttolocality_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobposttolocality_jobpostid on jobposttolocality (jobpostid);

alter table jobposttoskill add constraint fk_jobposttoskill_jobpostid foreign key (jobpostid) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_jobposttoskill_jobpostid on jobposttoskill (jobpostid);

alter table jobposttoskill add constraint fk_jobposttoskill_skillid foreign key (skillid) references skill (skillid) on delete restrict on update restrict;
create index ix_jobposttoskill_skillid on jobposttoskill (skillid);

# --- !Downs

alter table company drop foreign key fk_company_companylocality;
drop index ix_company_companylocality on company;

alter table company drop foreign key fk_company_comptype;
drop index ix_company_comptype on company;

alter table company drop foreign key fk_company_compstatus;
drop index ix_company_compstatus on company;

alter table jobapplication drop foreign key fk_jobapplication_jobpostid;
drop index ix_jobapplication_jobpostid on jobapplication;

alter table jobapplication drop foreign key fk_jobapplication_candidateid;
drop index ix_jobapplication_candidateid on jobapplication;

alter table jobpost drop foreign key fk_jobpost_jobstatus;
drop index ix_jobpost_jobstatus on jobpost;

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

alter table jobposttolocality drop foreign key fk_jobposttolocality_localityid;
drop index ix_jobposttolocality_localityid on jobposttolocality;

alter table jobposttolocality drop foreign key fk_jobposttolocality_jobpostid;
drop index ix_jobposttolocality_jobpostid on jobposttolocality;

alter table jobposttoskill drop foreign key fk_jobposttoskill_jobpostid;
drop index ix_jobposttoskill_jobpostid on jobposttoskill;

alter table jobposttoskill drop foreign key fk_jobposttoskill_skillid;
drop index ix_jobposttoskill_skillid on jobposttoskill;



drop table if exists company;

drop table if exists companystatus;

drop table if exists companytype;

drop table if exists experience;

drop table if exists jobapplication;

drop table if exists jobpost;

drop table if exists jobposttolocality;

drop table if exists jobposttoskill;

drop table if exists jobstatus;

