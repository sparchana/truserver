package dao;

import models.entity.ongrid.OnGridVerificationFields;
import models.entity.ongrid.OnGridVerificationStatus;

import java.util.List;

/**
 * Created by archana on 11/19/16.
 */
public class OnGridVerificationStatusDAO {

    public static final int STATUS_VERIFIED = 5;

    public OnGridVerificationStatus getByName(String statusName) {
        return OnGridVerificationStatus.find.where().eq("statusName", statusName).findUnique();
    }

    public OnGridVerificationStatus getById(Integer statusID) {
        return OnGridVerificationStatus.find.where().eq("statusId", statusID).findUnique();
    }

    public List<OnGridVerificationStatus> getAllStatus() {
        return OnGridVerificationStatus.find.all();
    }
}
