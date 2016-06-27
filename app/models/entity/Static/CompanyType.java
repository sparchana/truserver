package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 15/6/16.
 */
@Entity(name = "companytype")
@Table(name = "companytype")
public class CompanyType extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CompanyTypeId", columnDefinition = "bigint signed", unique = true)
    private Integer companyTypeId;

    @Column(name = "CompanyTypeName", columnDefinition = "varchar(100) not null")
    private String companyTypeName;

    public static Model.Finder<String, CompanyType> find = new Model.Finder(CompanyType.class);

    public Integer getCompanyTypeId() {
        return companyTypeId;
    }

    public void setCompanyTypeIdId(Integer companyTypeId) {
        this.companyTypeId = companyTypeId;
    }

    public String getCompanyTypeName() {
        return companyTypeName;
    }

    public void setCompanyTypeName(String companyTypeName) {
        this.companyTypeName = companyTypeName;
    }
}
