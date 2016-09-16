# --- !Ups

create table assessmentquestion (
	assessmentquestionid          int unsigned auto_increment not null,
	questiontext                  text null,
	questiontype                  int null,
	optiona                       text null,
	optionb                       text null,
	optionc                       text null,
	optiond                       text null,
	optione                       text null,
	answer                        text null,
  jobroleid                     bigint signed,
	constraint pk_assessmentquestion primary key (assessmentquestionid)
);

alter table assessmentquestion add constraint fk_assessmentquestion_jobroleid foreign key (jobroleid) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_assessmentquestion_jobroleid on assessmentquestion (jobroleid);


# --- !Downs

drop table if exists assessmentquestion;

alter table assessmentquestion drop foreign key fk_assessmentquestion_jobroleid;
drop index ix_assessmentquestion_jobroleid on assessmentquestion;
