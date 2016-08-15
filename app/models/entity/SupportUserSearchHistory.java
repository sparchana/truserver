package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "SupportUserSearchHistory")
@Table(name = "support_user_search_history")
public class SupportUserSearchHistory extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "support_user_search_history_id", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    private long supportUserSearchHistoryId = 0;

    @Column(name = "search_datetime", columnDefinition = "timestamp null")
    private Timestamp searchDateTime;

    @Column(name = "search_query", columnDefinition = "varchar(1000) null")
    private String searchQuery;

    @Column(name = "daily_search_sum", columnDefinition = "int signed null")
    private Integer dailySearchSum;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "developer_id", referencedColumnName = "DeveloperId")
    private Developer developer;

    public static Finder<String, SupportUserSearchHistory> find = new Finder(SupportUserSearchHistory.class);

    public long getSupportUserSearchHistoryId() {
        return supportUserSearchHistoryId;
    }

    public void setSupportUserSearchHistoryId(long supportUserSearchHistoryId) {
        this.supportUserSearchHistoryId = supportUserSearchHistoryId;
    }

    public Timestamp getSearchDateTime() {
        return searchDateTime;
    }

    public void setSearchDateTime(Timestamp searchDateTime) {
        this.searchDateTime = searchDateTime;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getDailySearchSum() {
        return dailySearchSum;
    }

    public void setDailySearchSum(Integer dailySearchSum) {
        this.dailySearchSum = dailySearchSum;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
}
