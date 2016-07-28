# --- !Ups
alter table candidate add column candidateplacelat double null;

alter table candidate add column candidateplacelng double null;



# --- !Downs

alter table candidate drop column candidateplacelat;

alter table candidate drop column candidateplacelng;