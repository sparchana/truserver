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

create table partner_to_candidate (
  partner_to_candidate_id       bigint signed auto_increment not null,
  partner_to_candidate_create_timestamp timestamp not null default current_timestamp,
  partner_id                    bigint signed,
  candidate_candidateid         bigint signed,
  partner_to_candidate_update_timestamp timestamp null,
  constraint uq_partner_to_candidate_candidate_candidateid unique (candidate_candidateid),
  constraint pk_partner_to_candidate primary key (partner_to_candidate_id)
);

alter table partner_to_candidate add constraint fk_partner_to_candidate_partner_id foreign key (partner_id) references partner (partner_id) on delete restrict on update restrict;
create index ix_partner_to_candidate_partner_id on partner_to_candidate (partner_id);

alter table partner_to_candidate add constraint fk_partner_to_candidate_candidate_candidateid foreign key (candidate_candidateid) references candidate (candidateid) on delete restrict on update restrict;

# --- !Downs

alter table partner_to_candidate drop foreign key fk_partner_to_candidate_partner_id;
drop index ix_partner_to_candidate_partner_id on partner_to_candidate;

alter table partner_to_candidate drop foreign key fk_partner_to_candidate_candidate_candidateid;

alter table assessmentquestion drop foreign key fk_assessmentquestion_jobroleid;
drop index ix_assessmentquestion_jobroleid on assessmentquestion;

drop table if exists partner_to_candidate;

drop table if exists assessmentquestion;


