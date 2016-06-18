package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "CompanyName", columnDefinition = "varchar(50) not null")
    private String companyName;

    @Column(name = "CompanyEmployeeCount", columnDefinition = "int signed null")
    private Integer companyEmployeeCount;

    @Column(name = "CompanyWebsite", columnDefinition = "varchar(30) null")
    private String companyWebsite;

    @Column(name = "CompanyDescription", columnDefinition = "varchar(500) null")
    private String companyDescription;

    @Column(name = "CompanyLogo", columnDefinition = "varchar(80) null")
    private String companyLogo;

    @Column(name = "CompanyCreateTimestamp", columnDefinition = "timestamp null")
    private Timestamp companyCreateTimestamp;

    @Column(name = "CompanyUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp companyUpdateTimestamp;

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

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<JobPost> jobPostList;

    public static Finder<String, Company> find = new Finder(Company.class);

    public Company() {
        this.companyUUId = UUID.randomUUID().toString();
        this.companyCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.companyUpdateTimestamp = new Timestamp(System.currentTimeMillis());
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

    public Integer getCompanyEmployeeCount() {
        return companyEmployeeCount;
    }

    public void setCompanyEmployeeCount(Integer companyEmployeeCount) {
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
}
