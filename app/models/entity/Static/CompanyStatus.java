package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 15/6/16.
 */
@Entity(name = "companystatus")
@Table(name = "companystatus")
public class CompanyStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CompanyStatusId", columnDefinition = "bigint signed", unique = true)
    private Integer companyStatusId;

    @Column(name = "CompanyStatusName", columnDefinition = "varchar(20) not null")
    private String companyStatusName;

    public static Model.Finder<String, CompanyStatus> find = new Model.Finder(CompanyStatus.class);

    public Integer getCompanyStatusId() {
        return companyStatusId;
    }

    public void setCompanyStatusId(Integer companyStatusId) {
        this.companyStatusId = companyStatusId;
    }

    public String getCompanyStatusName() {
        return companyStatusName;
    }

    public void setCompanyStatusName(String companyStatusName) {
        this.companyStatusName = companyStatusName;
    }
}
