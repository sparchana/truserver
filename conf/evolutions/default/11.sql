# --- !Ups
alter table locality add column placeid text null;

# --- !Downs
alter table locality drop column placeid;