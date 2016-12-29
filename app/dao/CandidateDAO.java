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

        String workFlowQueryBuilder = " select distinct objectauuid from interaction where" +
                "  interactiontype = '11' and objectatype = '4' and date(creationtimestamp) > curdate()-" + days ;

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("objectauuid", "objectAUUId")
                .create();

        List<Interaction> interactions = Ebean.find(Interaction.class)
                .setRawSql(rawSql)
                .findList();

        List<Candidate> candidateList = new ArrayList<>();

        for(Interaction interaction : interactions){
            candidateList.add(Candidate.find.where().eq("candidateUUId", interaction.getObjectAUUId()).findUnique());
        }

        return candidateList;
    }

    public static List<Candidate> getCandidateWithLessThanLimitProfileScore(int score) {

        return Candidate.find.where().lt("candidateScore", score).findList();
    }

}
