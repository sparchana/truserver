# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table candidateleads (
  candidateleadid               int signed not null auto_increment not null,
  candidateleadname             varchar(50) not null default 0 not null,
  candidateleadmobile           int signed not null default 0 not null,
  candidateleadtype             int signed not null default 0 not null,
  candidateleadchannel          int signed not null default 0 not null,
  candidateleadcreatetimestamp  timestamp default current_timestamp not null not null,
  candidateleadupdatetimestamp  timestamp not null not null,
  constraint pk_candidateleads primary key (candidateleadid)
);

create table channels (
  channelid                     int signed not null auto_increment not null,
  channelname                   varchar(50) not null default 0 not null,
  constraint pk_channels primary key (channelid)
);

create table leadtype (
  leadtypeid                    int signed not null auto_increment not null,
  leadtypename                  varchar(50) not null default 0 not null,
  constraint pk_leadtype primary key (leadtypeid)
);


# --- !Downs

drop table if exists candidateleads;

drop table if exists channels;

drop table if exists leadtype;

