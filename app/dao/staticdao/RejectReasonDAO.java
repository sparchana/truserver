package dao.staticdao;

import models.entity.Static.RejectReason;

import java.util.List;

/**
 * Created by dodo on 29/11/16.
 */
public class RejectReasonDAO {
    public RejectReason getById(Long reasonId) {
        return RejectReason.find.where().eq("reason_id", reasonId).findUnique();
    }

    public RejectReason getByName(String name) {
        return RejectReason.find.where().eq("reason_name", name).findUnique();
    }

    public List<RejectReason> getByType(Integer type) {
        return RejectReason.find.where().eq("reason_type", type).orderBy("reason_name").findList();
    }

    public List<RejectReason> getAll() {
        return RejectReason.find.all();
    }

}
