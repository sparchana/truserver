
# --- !Ups
create table asset (
  asset_id                      int unsigned auto_increment not null,
  asset_title                   varchar(255) null,
  is_common                     tinyint(1) not null default 0,
  constraint pk_asset primary key (asset_id)
);

create table job_role_to_asset (
  job_role_to_asset_id          int unsigned auto_increment not null,
  job_role_to_asset_uuid        varchar(255) not null not null,
  job_role_id                   bigint signed,
  asset_id                      int unsigned,
  creation_timestamp            timestamp default current_timestamp not null not null,
  constraint pk_job_role_to_asset primary key (job_role_to_asset_id)
);

create table job_role_to_document (
  job_role_to_document_id       bigint unsigned auto_increment not null,
  job_role_to_document_uuid     varchar(255) not null not null,
  job_role_id                   bigint signed,
  idproofid                     int signed,
  creation_timestamp            timestamp default current_timestamp not null not null,
  constraint pk_job_role_to_document primary key (job_role_to_document_id)
);

create table requirements_category (
  requirements_category_id      bigint unsigned auto_increment not null,
  requirements_category_uuid    varchar(255) not null not null,
  creation_timestamp            timestamp default current_timestamp not null not null,
  requirements_category_title   varchar(255) null,
  constraint pk_requirements_category primary key (requirements_category_id)
);

create table pre_screen_requirement (
  pre_screen_requirement_id     bigint unsigned auto_increment not null,
  pre_screen_requirement_uuid   varchar(255) not null not null,
  creation_timestamp            timestamp default current_timestamp not null not null,
  job_post_id                   bigint signed,
  flag                          tinyint(1) null,
  category                      int null not null,
  id_proof_id                   int signed,
  asset_id                      int unsigned,
  requirements_category_id      bigint unsigned,
  language_id                   int signed,
  constraint pk_pre_screen_requirement primary key (pre_screen_requirement_id)
);

create table job_post_asset_requirement (
  asset_requirement_id          bigint unsigned auto_increment not null,
  job_post_id                   bigint signed,
  asset_id                      int unsigned,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  constraint pk_job_post_asset_requirement primary key (asset_requirement_id)
);

create table job_post_document_requirement (
  job_post_document_id          bigint unsigned auto_increment not null,
  job_post_id                   bigint signed,
  id_proof_id                   int signed,
  create_timestamp              timestamp not null default current_timestamp,
  update_timestamp              timestamp null,
  constraint pk_job_post_document_requirement primary key (job_post_document_id)
);


alter table job_post_asset_requirement add constraint fk_job_post_asset_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_asset_requirement_job_post_id on job_post_asset_requirement (job_post_id);

alter table job_post_asset_requirement add constraint fk_job_post_asset_requirement_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_job_post_asset_requirement_asset_id on job_post_asset_requirement (asset_id);


alter table job_post_document_requirement add constraint fk_job_post_document_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_job_post_document_requirement_job_post_id on job_post_document_requirement (job_post_id);

alter table job_post_document_requirement add constraint fk_job_post_document_requirement_id_proof_id foreign key (id_proof_id) references idproof (idproofid) on delete restrict on update restrict;
create index ix_job_post_document_requirement_id_proof_id on job_post_document_requirement (id_proof_id);



alter table job_role_to_asset add constraint fk_job_role_to_asset_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_job_role_to_asset_job_role_id on job_role_to_asset (job_role_id);

alter table job_role_to_asset add constraint fk_job_role_to_asset_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_job_role_to_asset_asset_id on job_role_to_asset (asset_id);



alter table job_role_to_document add constraint fk_job_role_to_document_job_role_id foreign key (job_role_id) references jobrole (jobroleid) on delete restrict on update restrict;
create index ix_job_role_to_document_job_role_id on job_role_to_document (job_role_id);

alter table job_role_to_document add constraint fk_job_role_to_document_idproofid foreign key (idproofid) references idproof (idproofid) on delete restrict on update restrict;
create index ix_job_role_to_document_idproofid on job_role_to_document (idproofid);


alter table pre_screen_requirement add constraint fk_pre_screen_requirement_job_post_id foreign key (job_post_id) references jobpost (jobpostid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_job_post_id on pre_screen_requirement (job_post_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_id_proof_id foreign key (id_proof_id) references idproof (idproofid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_id_proof_id on pre_screen_requirement (id_proof_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_pre_screen_requirement_asset_id on pre_screen_requirement (asset_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_requirements_category_id foreign key (requirements_category_id) references requirements_category (requirements_category_id) on delete restrict on update restrict;
create index ix_pre_screen_requirement_requirements_category_id on pre_screen_requirement (requirements_category_id);

alter table pre_screen_requirement add constraint fk_pre_screen_requirement_language_id foreign key (language_id) references language (languageid) on delete restrict on update restrict;
create index ix_pre_screen_requirement_language_id on pre_screen_requirement (language_id);

alter table idproof add column is_common tinyint(1) not null default 0;

# --- !Downs

alter table job_post_asset_requirement drop foreign key fk_job_post_asset_requirement_job_post_id;
drop index ix_job_post_asset_requirement_job_post_id on job_post_asset_requirement;

alter table job_post_asset_requirement drop foreign key fk_job_post_asset_requirement_asset_id;
drop index ix_job_post_asset_requirement_asset_id on job_post_asset_requirement;

alter table job_post_document_requirement drop foreign key fk_job_post_document_requirement_job_post_id;
drop index ix_job_post_document_requirement_job_post_id on job_post_document_requirement;

alter table job_post_document_requirement drop foreign key fk_job_post_document_requirement_id_proof_id;
drop index ix_job_post_document_requirement_id_proof_id on job_post_document_requirement;


alter table job_role_to_asset drop foreign key fk_job_role_to_asset_job_role_id;
drop index ix_job_role_to_asset_job_role_id on job_role_to_asset;

alter table job_role_to_asset drop foreign key fk_job_role_to_asset_asset_id;
drop index ix_job_role_to_asset_asset_id on job_role_to_asset;



alter table job_role_to_document drop foreign key fk_job_role_to_document_job_role_id;
drop index ix_job_role_to_document_job_role_id on job_role_to_document;

alter table job_role_to_document drop foreign key fk_job_role_to_document_idproofid;
drop index ix_job_role_to_document_idproofid on job_role_to_document;


alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_job_post_id;
drop index ix_pre_screen_requirement_job_post_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_id_proof_id;
drop index ix_pre_screen_requirement_id_proof_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_asset_id;
drop index ix_pre_screen_requirement_asset_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_requirements_category_id;
drop index ix_pre_screen_requirement_requirements_category_id on pre_screen_requirement;

alter table pre_screen_requirement drop foreign key fk_pre_screen_requirement_language_id;
drop index ix_pre_screen_requirement_language_id on pre_screen_requirement;


alter table idproof drop column is_common;


drop table if exists job_post_document_requirement;

drop table if exists job_post_asset_requirement;

drop table if exists job_role_to_asset;

drop table if exists job_role_to_document;

drop table if exists pre_screen_requirement;

drop table if exists requirements_category;

drop table if exists asset;

