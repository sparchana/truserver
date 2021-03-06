package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.CompanyStatus;
import models.entity.Static.CompanyType;
import models.entity.Static.Locality;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by batcoder1 on 15/6/16.
 */
@Entity(name = "company")
@Table(name = "company")
public class Company extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CompanyId", columnDefinition = "bigint signed", unique = true)
    private Long companyId;

    @Column(name = "CompanyUUId", columnDefinition = "varchar(255) not null")
    private String companyUUId;

    @Column(name = "CompanyName", columnDefinition = "text not null")
    private String companyName;

    @Column(name = "CompanyEmployeeCount", columnDefinition = "varchar(15) signed null")
    private String companyEmployeeCount;

    @Column(name = "CompanyWebsite", columnDefinition = "text null")
    private String companyWebsite;

    @Column(name = "CompanyDescription", columnDefinition = "text null")
    private String companyDescription;

    @Column(name = "CompanyAddress", columnDefinition = "text null")
    private String companyAddress;

    @Column(name = "Latitude", columnDefinition = "double(10,6) null")
    private Double lat;

    @Column(name = "Longitude", columnDefinition = "double(10,6) null")
    private Double lng;

    @Column(name = "CompanyPinCode", columnDefinition = "bigint signed null")
    private Long companyPinCode;

    @Column(name = "CompanyLogo", columnDefinition = "text null")
    private String companyLogo;

    @Column(name = "CompanyCreateTimestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp companyCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "CompanyUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp companyUpdateTimestamp;

    @Column(name = "CompanyCode", columnDefinition = "varchar(20) null")
    private String companyCode;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompanyLocality")
    private Locality companyLocality;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompType")
    private CompanyType compType;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompStatus")
    private CompanyStatus compStatus;

    @JsonBackReference
    @PrivateOwned
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<JobPost> jobPostList;

    @JsonBackReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.ALL)
    private RecruiterProfile recruiterProfile;

    @Column(name = "Source", columnDefinition = "int null")
    private Integer source; // internal data

    public static Finder<String, Company> find = new Finder(Company.class);

    public Company() {
        this.companyUUId = UUID.randomUUID().toString();
        this.companyCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyUUId() {
        return companyUUId;
    }

    public void setCompanyUUId(String companyUUId) {
        this.companyUUId = companyUUId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmployeeCount() {
        return companyEmployeeCount;
    }

    public void setCompanyEmployeeCount(String companyEmployeeCount) {
        this.companyEmployeeCount = companyEmployeeCount;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Long getCompanyPinCode() {
        return companyPinCode;
    }

    public void setCompanyPinCode(Long companyPinCode) {
        this.companyPinCode = companyPinCode;
    }

    public List<JobPost> getJobPostList() {
        return jobPostList;
    }

    public void setJobPostList(List<JobPost> jobPostList) {
        this.jobPostList = jobPostList;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public Timestamp getCompanyCreateTimestamp() {
        return companyCreateTimestamp;
    }

    public void setCompanyCreateTimestamp(Timestamp companyCreateTimestamp) {
        this.companyCreateTimestamp = companyCreateTimestamp;
    }

    public Timestamp getCompanyUpdateTimestamp() {
        return companyUpdateTimestamp;
    }

    public void setCompanyUpdateTimestamp(Timestamp companyUpdateTimestamp) {
        this.companyUpdateTimestamp = companyUpdateTimestamp;
    }

    public Locality getCompanyLocality() {
        return companyLocality;
    }

    public void setCompanyLocality(Locality companyLocality) {
        this.companyLocality = companyLocality;
    }

    public CompanyType getCompType() {
        return compType;
    }

    public void setCompType(CompanyType compType) {
        this.compType = compType;
    }

    public CompanyStatus getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(CompanyStatus compStatus) {
        this.compStatus = compStatus;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}