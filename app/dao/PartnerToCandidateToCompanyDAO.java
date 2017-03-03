package dao;

import models.entity.Candidate;
import models.entity.OM.PartnerToCandidateToCompany;
import models.entity.Partner;

import java.util.List;

/**
 * Created by dodo on 3/2/17.
 */
public class PartnerToCandidateToCompanyDAO {
    public static PartnerToCandidateToCompany getPartnerCreatedCandidateById(Candidate candidate, List<Long> companyIdList){

        return PartnerToCandidateToCompany.find.where()
                .eq("partnerToCandidate.candidate.candidateId", candidate.getCandidateId())
                .in("partnerToCompany.company.companyId", companyIdList)
                .findUnique();
    }

    public static List<PartnerToCandidateToCompany> getPartnerCreatedCandidateList(Partner partner){

        return PartnerToCandidateToCompany.find.where()
                .eq("partner_id", partner.getPartnerId())
                .orderBy("creation_timestamp desc")
                .findList();
    }
}
