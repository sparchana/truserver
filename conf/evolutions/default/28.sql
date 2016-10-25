# --- !Ups

alter table recruiter_auth add constraint fk_auth_recruiterid foreign key (recruiter_id) references recruiterprofile (recruiterprofileid) on delete restrict on update restrict;

# --- !Downs

alter table recruiter_auth drop foreign key fk_auth_recruiterid;
