# --- !Ups

alter table candidate add column candidatescore int signed null;

# --- !Downs

alter table candidate drop column candidatescore;