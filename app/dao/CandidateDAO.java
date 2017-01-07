package dao;

import api.InteractionConstants;
import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.OM.JobPostWorkflow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import play.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

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
                "  interactiontype in (" + InteractionConstants.INTERACTION_TYPE_CANDIDATE_SIGN_UP + ", " +
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE + ", " +
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED + ")" +
                " and objectatype = " + ServerConstants.OBJECT_TYPE_CANDIDATE +
                " and date(creationtimestamp) > curdate()-" + days ;
        
        RawSql rawSql = RawSqlBuilder.parse(candidateQueryBuilder)
                .columnMapping("objectauuid", "objectAUUId")
                .create();

        List<Interaction> interactions = Ebean.find(Interaction.class)
                .setRawSql(rawSql)
                .findList();

        List<String> uuidList = new ArrayList<>();
        for(Interaction interaction : interactions){
            uuidList.add(interaction.getObjectAUUId());
        }

        return Candidate.find.where().in("candidateUUId", uuidList).findList();
    }

    public static List<Candidate> getAllActiveCandidateBeyondProvidedDays(Integer days) {

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        Date endDate = cal.getTime();

        Calendar calStart = new GregorianCalendar();
        calStart.add(Calendar.DAY_OF_MONTH, -(days + 30));

        Date startDate = calStart.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        String candidateQueryBuilder = " select distinct objectauuid from interaction " +
                "where objectatype = " + ServerConstants.OBJECT_TYPE_CANDIDATE +
                " and interactionchannel in (" + InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE +
                ", " + InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID +
                ", " + InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE +
                ") and creationtimestamp = " +
                "(select max(creationtimestamp) from interaction i " +
                "       where i.objectauuid = interaction.objectauuid) " +
                " and creationtimestamp < '" + sdf.format(endDate) + "'"+
                " and creationtimestamp > '" + sdf.format(startDate) + "'"  ;

        RawSql rawSql = RawSqlBuilder.parse(candidateQueryBuilder)
                .columnMapping("objectauuid", "objectAUUId")
                .create();

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
