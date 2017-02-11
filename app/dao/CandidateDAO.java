package dao;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.DeactivatedCandidateRequest;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import controllers.businessLogic.DeactivationService;
import models.entity.Candidate;
import models.entity.Interaction;

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

    public static List<Candidate> getNextDayDueDeActivatedCandidates() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        java.sql.Date tomorrow = new java.sql.Date(cal.getTimeInMillis());

        DeactivatedCandidateRequest deactivatedCandidateRequest = new DeactivatedCandidateRequest();
        deactivatedCandidateRequest.setFromThisDate(tomorrow);
        deactivatedCandidateRequest.setToThisDate(tomorrow);

        return DeactivationService.getDeActivatedCandidates(deactivatedCandidateRequest);
    }

    public static Candidate getById(long candidateId) {
        return Candidate.find.where().eq("candidateId", candidateId).findUnique();
    }

    public static int findByMobile(String mobile, int accessLevel) {
        if(mobile == null) return -1;
        return Candidate.find.where()
                .eq("candidateAccessLevel", accessLevel)
                .or(com.avaje.ebean.Expr.eq("candidateMobile",
                FormValidator.convertToIndianMobileFormat(mobile)),com.avaje.ebean.Expr.eq("candidateSecondMobile",
                FormValidator.convertToIndianMobileFormat(mobile))).findRowCount();
    }
}
