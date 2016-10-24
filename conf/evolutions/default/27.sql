# --- !Ups

ALTER TABLE interaction MODIFY COLUMN note text null;
ALTER TABLE interaction MODIFY COLUMN result text null;
ALTER TABLE recruiter_credit_history add column units int signed null;

# --- !Downs

ALTER TABLE interaction MODIFY COLUMN note varchar(255) null;
ALTER TABLE interaction MODIFY COLUMN result varchar(255) null;
ALTER TABLE recruiter_credit_history drop column units ;
