# --- !Ups
alter table jobpost add column source int null;
alter table company add column source int null;

# --- !Downs
alter table jobpost drop column source;
alter table company drop column source;