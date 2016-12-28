package dao;

import models.entity.Candidate;

import java.util.List;

/**
 * Created by dodo on 28/12/16.
 */

public class CandidateDAO {
    public static List<Candidate> getCandidateWithoutAndroidApp() {

        return Candidate.find.where()
                .isNull("CandidatePlaceLat")
                .findList();
    }
}
