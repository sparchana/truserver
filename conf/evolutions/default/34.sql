# --- !Ups

alter table candidate add column candidate_android_token text null;

# --- !Downs

alter table candidate drop column candidate_android_token;