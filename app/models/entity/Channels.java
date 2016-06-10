package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "channels")
@Table(name = "channels")
public class Channels extends Model{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ChannelId", columnDefinition = "int signed", unique = true)
    private long channelId = 0;

    @Column(name = "ChannelName", columnDefinition = "varchar(50) not null default 0", nullable = false)
    private String channelName = "";

    public static Finder<String, Channels> find = new Finder(Channels.class);


    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
