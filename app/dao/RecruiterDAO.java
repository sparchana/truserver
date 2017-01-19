package dao;

import models.entity.Recruiter.RecruiterProfile;

/**
 * Created by zero on 19/1/17.
 */
public class RecruiterDAO {
    public static RecruiterProfile findById(Long recruiterId) {
        if(recruiterId == null) return null;
        return RecruiterProfile.find.where().eq("recruiterProfileId", recruiterId).findUnique();
    }
}
