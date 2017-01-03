package api.http.httpResponse.hirewand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Location {
            public String city;
            public String country;
            public Integer zipcode;
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Education {

            @JsonIgnoreProperties(ignoreUnknown=true)
            public class College {
                public String name;
                public String city;
                public String tier;
            }
            public String score;
            public String degree;
            public String level;
            public College college;
            public List<String> major;
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class WorkExperience {
            public class Skill{
                public String skill;
                public Integer duration;
            }
            public List<String> role;
            public Date start;
            public Date end;
            public List<String> company;
            public List<Skill> skill;
            public List<String> city;
            public Integer duration;
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class ProjectExperience{
            public String title;
            public List<String> role;
            public Date start;
            public Date end;
            public List<String> client;
            public List<WorkExperience.Skill> skill;
            public List<String> city;
            public Integer duration;
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class PersonalDetails{
            public Boolean married;
            public Date dateofbirth;
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
        public Date UpdateDate;
        public Long UpdateDateMS;
        public String Source;
        public List<SkillRating> SkillRatings;
        public List<Location> Locations;
        public List<Education> Education;
        public List<WorkExperience> WorkExperience;
        public List<ProjectExperience> ProjectExperience;
        public PersonalDetails PersonalDetails;
        public String ProfileJSON;
    }

    public String status;
    public String message;
    public Boolean duplicate;
    public String personid;
    public Integer accountid;
    public Profile profile;

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
