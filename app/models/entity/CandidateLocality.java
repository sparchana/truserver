package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 29/4/16.
 */
@Entity(name = "candidatelocality")
@Table(name = "candidatelocality")
public class CandidateLocality extends Model {
    @Id
    @Column(name = "CandidateLocalityId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateLocalityId = 0;

    @Column(name = "CandidateLocalityCandidateId", columnDefinition = "bigint not null default 0")
    public long candidateLocalityCandidateId = 0;

    @Column(name = "CandidateLocalityLocalityId", columnDefinition = "varchar(4) not null default 0")
    public String candidateLocalityLocalityId = "";

    public static Model.Finder<String, CandidateLocality> find = new Model.Finder(CandidateLocality.class);
}