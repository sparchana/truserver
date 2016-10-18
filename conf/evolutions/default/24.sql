# --- !Ups

create table pre_screen_response (
  pre_screen_response_id        bigint unsigned auto_increment not null,
  pre_screen_response_uuid      varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  update_timestamp              timestamp null,
  pre_screen_result_id          bigint unsigned,
  pre_screen_requirement_id     bigint unsigned,
  pre_screen_response           tinyint(1) null,
  constraint pk_pre_screen_response primary key (pre_screen_response_id)
);



create table pre_screen_result (
  pre_screen_result_id          bigint unsigned auto_increment not null,
  pre_screen_result_uuid        varchar(255) not null,
  create_timestamp              timestamp default current_timestamp not null,
  attempt_count                 int null,
  result_score                  double(2,2) null,
  force_set                     tinyint(1) null,
  update_timestamp              timestamp null,
  job_post_workflow_id          bigint unsigned,
  constraint pk_pre_screen_result primary key (pre_screen_result_id)
);

alter table pre_screen_response add constraint fk_pre_screen_response_pre_screen_result_id foreign key (pre_screen_result_id) references pre_screen_result (pre_screen_result_id) on delete restrict on update restrict;
create index ix_pre_screen_response_pre_screen_result_id on pre_screen_response (pre_screen_result_id);

alter table pre_screen_response add constraint fk_pre_screen_response_pre_screen_requirement_id foreign key (pre_screen_requirement_id) references pre_screen_requirement (pre_screen_requirement_id) on delete restrict on update restrict;
create index ix_pre_screen_response_pre_screen_requirement_id on pre_screen_response (pre_screen_requirement_id);

alter table pre_screen_result add constraint fk_pre_screen_result_job_post_workflow_id foreign key (job_post_workflow_id) references job_post_workflow (job_post_workflow_id) on delete restrict on update restrict;
create index ix_pre_screen_result_job_post_workflow_id on pre_screen_result (job_post_workflow_id);


# --- !Downs

alter table pre_screen_response drop foreign key fk_pre_screen_response_pre_screen_result_id;
drop index ix_pre_screen_response_pre_screen_result_id on pre_screen_response;

alter table pre_screen_response drop foreign key fk_pre_screen_response_pre_screen_requirement_id;
drop index ix_pre_screen_response_pre_screen_requirement_id on pre_screen_response;

alter table pre_screen_result drop foreign key fk_pre_screen_result_job_post_workflow_id;
drop index ix_pre_screen_result_job_post_workflow_id on pre_screen_result;

drop table if exists pre_screen_response;

drop table if exists pre_screen_result;
