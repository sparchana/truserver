package dao.staticdao;

import models.entity.Static.TrudroidFeedbackReason;

import java.util.List;

/**
 * Created by dodo on 6/12/16.
 */
public class TrudroidFeedbackReasonDAO {
    public TrudroidFeedbackReason getById(Long reasonId) {
        return TrudroidFeedbackReason.find.where().eq("reason_id", reasonId).findUnique();
    }

    public TrudroidFeedbackReason getByName(String name) {
        return TrudroidFeedbackReason.find.where().eq("reason_name", name).findUnique();
    }

    public List<TrudroidFeedbackReason> getByType(Integer type) {
        return TrudroidFeedbackReason.find.where().eq("reason_type", type).orderBy("reason_name").findList();
    }

    public List<TrudroidFeedbackReason> getAll() {
        return TrudroidFeedbackReason.find.all();
    }

}
