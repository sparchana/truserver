# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table candidate (
  candidateid                   int signed not null auto_increment not null,
  candidateuuid                 varchar(255) not null not null,
  leadid                        int signed not null,
  candidatename                 varchar(50) not null,
  candidatemobile               varchar(13) not null,
  candidatetype                 int signed not null default 0,
  candidatechannel              int signed not null default 0,
  candidatejobinterest          varchar(255) null ,
  candidatecreatetimestamp      timestamp default current_timestamp not null,
  candidateupdatetimestamp      timestamp not null,
  constraint uq_candidate_candidateuuid unique (candidateuuid),
  constraint uq_candidate_leadid unique (leadid),
  constraint pk_candidate primary key (candidateid)
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


# --- !Downs

drop table if exists candidate;

drop table if exists channels;

drop table if exists developer;

drop table if exists interaction;

drop table if exists lead;

drop table if exists leadtype;

