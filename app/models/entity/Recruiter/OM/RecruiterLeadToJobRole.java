package models.entity.Recruiter.OM;

import api.http.httpRequest.Recruiter.RecruiterLeadToJobRoleRequest;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.JobRole;
import models.util.Message;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static models.util.Util.ACTION_CREATE;
import static models.util.Util.ACTION_UPDATE;

/**
 * Created by dodo on 5/10/16.
 */

@Entity(name = "recruiterleadtojobrole")
@Table(name = "recruiterleadtojobrole")
public class RecruiterLeadToJobRole extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_lead_to_job_role_id", columnDefinition = "bigint signed", unique = true)
    private Long recruiterLeadToJobRoleId;

    @Column(name = "recruiter_lead_to_job_role_uuid", columnDefinition = "varchar(255) not null", unique = true)
    private String recruiterLeadToJobRoleUUId;

    @CreatedTimestamp
    @Column(name = "recruiter_lead_to_job_role_create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterLeadToJobRoleCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "recruiter_lead_to_job_role_update_timeStamp", columnDefinition = "timestamp")
    private Timestamp recruiterLeadToJobRoleUpdateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recruiter_lead_id", referencedColumnName = "recruiter_lead_id")
    private RecruiterLead recruiterLead;

    @Column(name = "job_interview_address", columnDefinition = "varchar(255) null")
    private String jobInterviewAddress;

    @Column(name = "job_vacancies", columnDefinition = "bigint null")
    private Long jobVacancies;

    @Column(name = "job_salary_min", columnDefinition = "bigint null")
    private Long jobSalaryMin;

    @Column(name = "job_salary_max", columnDefinition = "bigint null")
    private Long jobSalaryMax;

    @Column(name = "job_gender", columnDefinition = "varchar(2) null")
    private String jobGender;

    @Column(name = "job_detail_requirement", columnDefinition = "varchar(255) null")
    private String jobDetailRequirement;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterLeadToJobRole", cascade = CascadeType.ALL)
    private List<RecruiterLeadToLocality> recruiterLeadToLocalityList;

    public static Finder<String, RecruiterLeadToJobRole> find = new Finder(RecruiterLeadToJobRole.class);

    public RecruiterLeadToJobRole() {
        this.recruiterLeadToJobRoleUUId = UUID.randomUUID().toString();
    }

    public Long getRecruiterLeadToJobRoleId() {
        return recruiterLeadToJobRoleId;
    }

    public void setRecruiterLeadToJobRoleId(Long recruiterLeadToJobRoleId) {
        this.recruiterLeadToJobRoleId = recruiterLeadToJobRoleId;
    }

    public Timestamp getRecruiterLeadToJobRoleCreateTimeStamp() {
        return recruiterLeadToJobRoleCreateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleCreateTimeStamp(Timestamp recruiterLeadToJobRoleCreateTimeStamp) {
        this.recruiterLeadToJobRoleCreateTimeStamp = recruiterLeadToJobRoleCreateTimeStamp;
    }

    public Timestamp getRecruiterLeadToJobRoleUpdateTimeStamp() {
        return recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleUpdateTimeStamp(Timestamp recruiterLeadToJobRoleUpdateTimeStamp) {
        this.recruiterLeadToJobRoleUpdateTimeStamp = recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public RecruiterLead getRecruiterLead() {
        return recruiterLead;
    }

    public void setRecruiterLead(RecruiterLead recruiterLead) {
        this.recruiterLead = recruiterLead;
    }

    public List<RecruiterLeadToLocality> getRecruiterLeadToLocalityList() {
        return recruiterLeadToLocalityList;
    }

    public void setRecruiterLeadToLocalityList(List<RecruiterLeadToLocality> recruiterLeadToLocalityList) {
        this.recruiterLeadToLocalityList = recruiterLeadToLocalityList;
    }

    public List<RecruiterLeadToJobRole> readById(List<Long> ids) {
        return RecruiterLeadToJobRole.find.where().idIn(ids).setUseCache(Boolean.TRUE).findList();
    }

    public List<RecruiterLeadToJobRole> readByUUID(List<Long> uuids) {
        return RecruiterLeadToJobRole.find.where().in("recruiter_lead_to_job_role_uuid",uuids).setUseCache(Boolean.TRUE).findList();
    }

    public String getRecruiterLeadToJobRoleUUId() {
        return recruiterLeadToJobRoleUUId;
    }

    public void setRecruiterLeadToJobRoleUUId(String recruiterLeadToJobRoleUUId) {
        this.recruiterLeadToJobRoleUUId = recruiterLeadToJobRoleUUId;
    }

    public String getJobInterviewAddress() {
        return jobInterviewAddress;
    }

    public void setJobInterviewAddress(String jobInterviewAddress) {
        this.jobInterviewAddress = jobInterviewAddress;
    }

    public Long getJobVacancies() {
        return jobVacancies;
    }

    public void setJobVacancies(Long jobVacancies) {
        this.jobVacancies = jobVacancies;
    }

    public Long getJobSalaryMin() {
        return jobSalaryMin;
    }

    public void setJobSalaryMin(Long jobSalaryMin) {
        this.jobSalaryMin = jobSalaryMin;
    }

    public Long getJobSalaryMax() {
        return jobSalaryMax;
    }

    public void setJobSalaryMax(Long jobSalaryMax) {
        this.jobSalaryMax = jobSalaryMax;
    }

    public String getJobGender() {
        return jobGender;
    }

    public void setJobGender(String jobGender) {
        this.jobGender = jobGender;
    }

    public String getJobDetailRequirement() {
        return jobDetailRequirement;
    }

    public void setJobDetailRequirement(String jobDetailRequirement) {
        this.jobDetailRequirement = jobDetailRequirement;
    }

    public List<Message> validateJobVacancies(RecruiterLeadToJobRoleRequest request, String action, RecruiterLeadToJobRole entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:
                if(request.getJobVacancies() == null || request.getJobVacancies() == 0) {
                    try {
                        messageList.add(new Message(Message.MESSAGE_WARNING,"Please specify number of vacancies"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        return messageList;
    }

}


