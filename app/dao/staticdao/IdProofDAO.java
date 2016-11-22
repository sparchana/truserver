package dao.staticdao;

import models.entity.Static.IdProof;
import models.entity.ongrid.OnGridVerificationFields;

import java.util.List;
import java.util.Map;

/**
 * Created by archana on 11/19/16.
 */
public class IdProofDAO {

    public static final int IDPROOF_AADHAAR_ID = 3;

    public static IdProof getIdProofRecord(int idProofId) {
        return IdProof.find.where().eq("idProofId", idProofId).findUnique();
    }

    public static Map<?, IdProof> getIdProofRecordList(List<Integer> idProofIds) {
        return IdProof.find.where().in("idProofId", idProofIds).setMapKey("idProofId").findMap();
    }

    public static List<IdProof> getAllIdProofRecords() {
        return IdProof.find.all();
    }
}
