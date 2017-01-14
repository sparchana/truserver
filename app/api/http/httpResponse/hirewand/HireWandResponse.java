package api.http.httpResponse.hirewand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.entity.Static.Skill;
import play.Logger;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 29-12-2016.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class HireWandResponse {

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Profile{

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class SkillRating {
            public String skill;
            public Integer duration;
            public Integer rating;

            public String getSkill() {
                return skill;
            }

            public void setSkill(String skill) {
                this.skill = skill;
            }

            public Integer getDuration() {
                return duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }

            public Integer getRating() {
                return rating;
            }

            public void setRating(Integer rating) {
                this.rating = rating;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Location {
            public String city;
            public String country;
            public String zip;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getZip() {
                return zip;
            }

            public void setZip(String zip) {
                this.zip = zip;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Education {

            @JsonIgnoreProperties(ignoreUnknown=true)
            public static class College {
                public String name;
                public String city;
                public String tier;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getTier() {
                    return tier;
                }

                public void setTier(String tier) {
                    this.tier = tier;
                }
            }
            public String score;
            public String degree;
            public String level;
            public College college;
            public List<String> major;

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getDegree() {
                return degree;
            }

            public void setDegree(String degree) {
                this.degree = degree;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public College getCollege() {
                return college;
            }

            public void setCollege(College college) {
                this.college = college;
            }

            public List<String> getMajor() {
                return major;
            }

            public void setMajor(List<String> major) {
                this.major = major;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class WorkExperience {
            public static class Skill{
                public String skill;
                public Integer duration;

                public String getSkill() {
                    return skill;
                }

                public void setSkill(String skill) {
                    this.skill = skill;
                }

                public Integer getDuration() {
                    return duration;
                }

                public void setDuration(Integer duration) {
                    this.duration = duration;
                }
            }
            public List<String> role;
            public String start;
            public String end;
            public List<String> company;
            public List<Skill> skill;
            public List<String> city;
            public Integer duration;

            public List<String> getRole() {
                return role;
            }

            public void setRole(List<String> role) {
                this.role = role;
            }

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public List<String> getCompany() {
                return company;
            }

            public void setCompany(List<String> company) {
                this.company = company;
            }

            public List<Skill> getSkill() {
                return skill;
            }

            public void setSkill(List<Skill> skill) {
                this.skill = skill;
            }

            public List<String> getCity() {
                return city;
            }

            public void setCity(List<String> city) {
                this.city = city;
            }

            public Integer getDuration() {
                return duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class ProjectExperience{
            public String title;
            public List<String> company;
            public List<String> role;
            public String start;
            public String end;
            public List<String> client;
            public List<String> skill;
            public List<String> city;
            public Integer duration;

            public List<String> getCompany() {
                return company;
            }

            public void setCompany(List<String> company) {
                this.company = company;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<String> getRole() {
                return role;
            }

            public void setRole(List<String> role) {
                this.role = role;
            }

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public List<String> getClient() {
                return client;
            }

            public void setClient(List<String> client) {
                this.client = client;
            }

            public List<String> getSkill() {
                return skill;
            }

            public void setSkill(List<String> skill) {
                this.skill = skill;
            }

            public List<String> getCity() {
                return city;
            }

            public void setCity(List<String> city) {
                this.city = city;
            }

            public Integer getDuration() {
                return duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class PersonalDetails{
            public Boolean married;
            public String dateofbirth;

            public Boolean getMarried() {
                return married;
            }

            public void setMarried(Boolean married) {
                this.married = married;
            }

            public String getDateofbirth() {
                return dateofbirth;
            }

            public void setDateofbirth(String dateofbirth) {
                this.dateofbirth = dateofbirth;
            }
        }

        public String Name;
        public List<String> Emails;
        public List<String> LatestRoles;
        public List<String> LatestCompanies;
        public String Gender;
        public List<String> PhoneNos;
        public List<String> Companies;
        public Integer TotalExperience;
        public String Resumepath;
        public String UpdateDate;
        public Long UpdateDateMS;
        public String Source;
        public List<SkillRating> SkillRatings;
        public List<Location> Locations;
        public List<Education> Education;
        public List<WorkExperience> WorkExperience;
        public List<ProjectExperience> ProjectExperience;
        public PersonalDetails PersonalDetails;
        public String ProfileJSON;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public List<String> getEmails() {
            return Emails;
        }

        public void setEmails(List<String> emails) {
            Emails = emails;
        }

        public List<String> getLatestRoles() {
            return LatestRoles;
        }

        public void setLatestRoles(List<String> latestRoles) {
            LatestRoles = latestRoles;
        }

        public List<String> getLatestCompanies() {
            return LatestCompanies;
        }

        public void setLatestCompanies(List<String> latestCompanies) {
            LatestCompanies = latestCompanies;
        }

        public String getGender() {
            return Gender;
        }

        public void setGender(String gender) {
            Gender = gender;
        }

        public List<String> getPhoneNos() {
            return PhoneNos;
        }

        public void setPhoneNos(List<String> phoneNos) {
            PhoneNos = phoneNos;
        }

        public List<String> getCompanies() {
            return Companies;
        }

        public void setCompanies(List<String> companies) {
            Companies = companies;
        }

        public Integer getTotalExperience() {
            return TotalExperience;
        }

        public void setTotalExperience(Integer totalExperience) {
            TotalExperience = totalExperience;
        }

        public String getResumepath() {
            return Resumepath;
        }

        public void setResumepath(String resumepath) {
            Resumepath = resumepath;
        }

        public String getUpdateDate() {
            return UpdateDate;
        }

        public void setUpdateDate(String updateDate) {
            UpdateDate = updateDate;
        }

        public Long getUpdateDateMS() {
            return UpdateDateMS;
        }

        public void setUpdateDateMS(Long updateDateMS) {
            UpdateDateMS = updateDateMS;
        }

        public String getSource() {
            return Source;
        }

        public void setSource(String source) {
            Source = source;
        }

        public List<SkillRating> getSkillRatings() {
            return SkillRatings;
        }

        public void setSkillRatings(List<SkillRating> skillRatings) {
            SkillRatings = skillRatings;
        }

        public List<Location> getLocations() {
            return Locations;
        }

        public void setLocations(List<Location> locations) {
            Locations = locations;
        }

        public List<Profile.Education> getEducation() {
            return Education;
        }

        public void setEducation(List<Profile.Education> education) {
            Education = education;
        }

        public List<Profile.WorkExperience> getWorkExperience() {
            return WorkExperience;
        }

        public void setWorkExperience(List<Profile.WorkExperience> workExperience) {
            WorkExperience = workExperience;
        }

        public List<Profile.ProjectExperience> getProjectExperience() {
            return ProjectExperience;
        }

        public void setProjectExperience(List<Profile.ProjectExperience> projectExperience) {
            ProjectExperience = projectExperience;
        }

        public Profile.PersonalDetails getPersonalDetails() {
            return PersonalDetails;
        }

        public void setPersonalDetails(Profile.PersonalDetails personalDetails) {
            PersonalDetails = personalDetails;
        }

        public String getProfileJSON() {
            return ProfileJSON;
        }

        public void setProfileJSON(String profileJSON) {
            ProfileJSON = profileJSON;
        }
    }

    public String status;
    public String message;
    public Boolean duplicate;
    public String personid;
    public Integer accountid;
    public Profile profile;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public Integer getAccountid() {
        return accountid;
    }

    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException e ) {
                Logger.info(e.toString());
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
