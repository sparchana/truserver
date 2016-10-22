# --- !Ups

create table candidate_asset (
  candidate_asset_id            bigint unsigned auto_increment not null,
  create_timestamp              timestamp default current_timestamp not null,
  candidateid                   bigint signed,
  asset_id                      int unsigned,
  updatetimestamp               timestamp null,
  constraint pk_candidate_asset primary key (candidate_asset_id)
);

alter table candidate_asset add constraint fk_candidate_asset_candidateid foreign key (candidateid) references candidate (candidateid) on delete restrict on update restrict;
create index ix_candidate_asset_candidateid on candidate_asset (candidateid);

alter table candidate_asset add constraint fk_candidate_asset_asset_id foreign key (asset_id) references asset (asset_id) on delete restrict on update restrict;
create index ix_candidate_asset_asset_id on candidate_asset (asset_id);

alter table pre_screen_result add column pre_screen_result_note text null;

# --- !Downs

alter table candidate_asset drop foreign key fk_candidate_asset_candidateid;
drop index ix_candidate_asset_candidateid on candidate_asset;

alter table candidate_asset drop foreign key fk_candidate_asset_asset_id;
drop index ix_candidate_asset_asset_id on candidate_asset;

alter table pre_screen_result drop column pre_screen_result_note;

drop table if exists candidate_asset;