# --- !Ups
alter table candidate add column candidateplacelat double null;
alter table candidate add column candidateplacelng double null;

alter table jobposttolocality add column latitude double null;
alter table jobposttolocality add column longitude double null;

alter table jobrole add column jobroleicon varchar(255) null;

alter table jobpost add column gender int(1) null;
# --- !Downs

alter table candidate drop column candidateplacelat;
alter table candidate drop column candidateplacelng;

alter table jobposttolocality drop column latitude;
alter table jobposttolocality drop column longitude;

alter table jobrole drop column jobroleicon;

alter table jobpost drop column gender;