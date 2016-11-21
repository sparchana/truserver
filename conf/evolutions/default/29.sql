# --- !Ups
alter table idproofreference add column idproofnumber varchar(255) null;

# --- !Downs
alter table idproofreference drop column idproofnumber;