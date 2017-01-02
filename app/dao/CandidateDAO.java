package dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.OM.JobPostWorkflow;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dodo on 28/12/16.
 */

public class CandidateDAO {
    public static List<Candidate> getCandidateWithoutAndroidApp() {

        return Candidate.find.where()
                .isNull("CandidatePlaceLat")
                .findList();
    }
    public static List<Candidate> getCandidateWhoUpdateProfileSinceIndexDays(Integer days) {

        String candidateQueryBuilder = " select distinct objectauuid from interaction where" +
                "  interactiontype in (10, 11, 12) and objectatype = '4' and date(creationtimestamp) > curdate()-" + days ;

        RawSql rawSql = RawSqlBuilder.parse(candidateQueryBuilder)
                .columnMapping("objectauuid", "objectAUUId")
                .create();

        List<Interaction> interactions = Ebean.find(Interaction.class)
                .setRawSql(rawSql)
                .findList();

        List<Candidate> candidateList = new ArrayList<>();

        for(Interaction interaction : interactions){
            Candidate candidate = Candidate.find.where().eq("candidateUUId", interaction.getObjectAUUId()).findUnique();
            if(candidate != null){
                candidateList.add(candidate);
            }
        }

        return candidateList;
    }

    public static List<Candidate> getAllActiveCandidateWithinProvidedDays(Integer days) {

        String candidateQueryBuilder = " select distinct objectauuid from interaction where" +
                "  objectatype = '4' and date(creationtimestamp) > curdate()-" + days ;

        RawSql rawSql = RawSqlBuilder.parse(candidateQueryBuilder)
                .columnMapping("objectauuid", "objectAUUId")
                .create();

        Logger.info(candidateQueryBuilder);
        List<Interaction> interactions = Ebean.find(Interaction.class)
                .setRawSql(rawSql)
                .findList();

        List<String> uuidList = new ArrayList<>();
        for(Interaction interaction : interactions){
            uuidList.add(interaction.getObjectAUUId());
        }

        return Candidate.find.where().in("candidateUUId", uuidList).findList();
    }
}
