# --- !Ups
drop table if exists business_lead;

# --- !Downs

create table business_lead (
  leadid                        bigint auto_increment not null,
  leaduuid                      varchar(255) not null,
  sourcetype                    varchar(255) not null not null,
  sourcename                    varchar(255) null,
  sourcedate                    date null,
  jobrole                       varchar(255) not null not null,
  salarymin                     bigint null,
  salarymax                     bigint null,
  gender                        varchar(255) null,
  locality                      varchar(255) null,
  vacancies                     bigint null,
  companyname                   varchar(255) null,
  contactname                   varchar(255) null,
  primarynumber                 varchar(255) not null not null,
  altnumber                     varchar(255) null,
  email                         varchar(255) null,
  address                       varchar(255) null,
  detailrequirement             varchar(255) null,
  createdon                     timestamp not null DEFAULT CURRENT_TIMESTAMP,
  createdby                     varchar(255) not null,
  creationchannel               varchar(255) not null,
  editedon                      timestamp null,
  editedby                      varchar(255) null,
  constraint uq_business_lead_leaduuid unique (leaduuid),
  constraint pk_business_lead primary key (leadid)
);
