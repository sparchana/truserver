# --- !Ups
alter table lead add column followupid int null;

create table followup (
	followupid                    int signed auto_increment not null,
	followupmobile                varchar(13) not null,
	followupstatus                tinyint(1) not null,
	followuptimestamp             timestamp null,
	followupcreationtimestamp     timestamp not null,
	followupupdatetimestamp       timestamp null,
constraint pk_followup primary key (followupid)
);

alter table lead add constraint fk_lead_followupid foreign key (followupid) references followup (followupid) on delete restrict on update restrict;

# --- !Downs
alter table lead drop foreign key fk_lead_followupid;

alter table lead drop column followupid;

drop table if exists followup;

