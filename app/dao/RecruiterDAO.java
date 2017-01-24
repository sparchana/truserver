package dao;

import models.entity.Recruiter.RecruiterProfile;

import java.util.List;
import java.util.Map;

/**
 * Created by zero on 19/1/17.
 */
public class RecruiterDAO {
    public static RecruiterProfile findById(Long recruiterId) {
        if(recruiterId == null) return null;
        return RecruiterProfile.find.where().eq("recruiterProfileId", recruiterId).findUnique();
    }

    public static List<RecruiterProfile> findListByCompanyId(Long companyId) {
        if(companyId == null) return null;
        return RecruiterProfile.find.where().eq("company.companyId", companyId).findList();
    }

    public static Map<?, RecruiterProfile> findMapByCompanyId(Long companyId) {
        if(companyId == null) return null;
        return RecruiterProfile.find.where().eq("company.companyId", companyId).setMapKey("recruiterProfileId").findMap();
    }
}
