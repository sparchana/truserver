# --- !Ups

alter table interaction add column interactionchannel int not null default 0;

# --- !Downs

alter table interaction drop column interactionchannel;