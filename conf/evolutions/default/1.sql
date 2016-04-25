# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table candidate (
  candidateid                   int signed not null auto_increment not null,
  candidatestatusid             int signed not null default 0,
  candidatename                 varchar(50) not null default 0,
  candidatemobile               varchar(10) not null default 0,
  candidateage                  int signed not null default 0,
  candidatecreatetimestamp      timestamp default current_timestamp not null,
  candidateupdatetimestamp      timestamp not null default 0,
  candidateotpid                int signed not null default 1234,
  constraint pk_candidate primary key (candidateid)
);

create table channels (
  channelid                     int signed not null auto_increment not null,
  channelname                   varchar(50) not null default 0 not null,
  constraint pk_channels primary key (channelid)
);

create table leads (
  leadid                        int signed not null auto_increment not null,
  leadname                      varchar(50) not null,
  leadmobile                    varchar(10) not null ,
  leadchannel                   int signed not null,
  leadtype                      int signed not null,
  leadinterest                  varchar(30) not null ,
  leadcreatetimestamp           timestamp default current_timestamp not null,
  constraint pk_leads primary key (leadid)
);

create table leadtype (
  leadtypeid                    int signed not null auto_increment not null,
  leadtypename                  varchar(50) not null default 0 not null,
  constraint pk_leadtype primary key (leadtypeid)
);


# --- !Downs

drop table if exists candidate;

drop table if exists channels;

drop table if exists leads;

drop table if exists leadtype;

