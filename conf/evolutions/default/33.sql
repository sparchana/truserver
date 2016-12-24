# --- !Ups

create table candidate_resume (
  candidate_resume_id           bigint unsigned auto_increment not null,
  created_by                    varchar(255) not null,
  candidateid                   bigint signed,
  file_path                     varchar(512) not null,
  external_key                  varchar(255) not null,
  parsed_resume                 text,
  create_timestamp              timestamp default current_timestamp not null,
  constraint pk_candidate_resume primary key (candidate_resume_id)
);

alter table candidate_resume add constraint fk_candidate_resume_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_resume_candidateid on candidate_resume (candidateid);


# --- !Downs

alter table candidate_resume drop foreign key fk_candidate_resume_candidateid;
drop index ix_candidate_resume_candidateid on candidate_resume;

drop table if exists candidate_resume;
