package dao.staticdao;

import models.entity.Static.CandidateFeedbackReason;

import java.util.List;

/**
 * Created by dodo on 6/12/16.
 */
public class TrudroidFeedbackReasonDAO {
    public CandidateFeedbackReason getById(Long reasonId) {
        return CandidateFeedbackReason.find.where().eq("reason_id", reasonId).findUnique();
    }

    public CandidateFeedbackReason getByName(String name) {
        return CandidateFeedbackReason.find.where().eq("reason_name", name).findUnique();
    }

    public List<CandidateFeedbackReason> getByType(Integer type) {
        return CandidateFeedbackReason.find.where().eq("reason_type", type).orderBy("reason_name").findList();
    }

    public List<CandidateFeedbackReason> getAll() {
        return CandidateFeedbackReason.find.all();
    }

}
