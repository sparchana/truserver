package api.http.httpRequest.Workflow.preScreenEdit;

import java.util.List;

/**
 * Created by zero on 10/11/16.
 */
public class UpdateCandidateDocument {

    public class IdProofWithIdNumber {
        Integer idProofId;
        String idNumber;

        public IdProofWithIdNumber() {
        }

        public IdProofWithIdNumber(UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber) {
            //constructor Code
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public Integer getIdProofId() {
            return idProofId;
        }

        public void setIdProofId(Integer idProofId) {
            this.idProofId = idProofId;
        }
    }

    public List<IdProofWithIdNumber> idProofWithIdNumberList;

    public List<IdProofWithIdNumber> getIdProofWithIdNumberList() {
        return idProofWithIdNumberList;
    }

    public void setIdProofWithIdNumberList(List<IdProofWithIdNumber> idProofWithIdNumberList) {
        this.idProofWithIdNumberList = idProofWithIdNumberList;
    }
}
