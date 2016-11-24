package dao.staticdao;

import com.avaje.ebean.Model;
import models.entity.Static.IdProof;

import java.util.List;
import java.util.Map;

/**
 * Created by archana on 11/19/16.
 */
public class IdProofDAO  {

    public static final int IDPROOF_AADHAAR_ID = 3;

    public IdProof getById(Integer idProofId) {
        return IdProof.find.where().eq("idProofId", idProofId).findUnique();
    }

    public IdProof getByName(String name) {
        return IdProof.find.where().eq("idProofName", name).findUnique();
    }

    public Map<Integer, IdProof> getIdToRecordMap(List<Integer> idProofIds) {
        return IdProof.find.where().in("idProofId", idProofIds).setMapKey("idProofId").findMap("idProofId", Integer.class);
    }

    public List<IdProof> getAll() {
        return IdProof.find.all();
    }

}
