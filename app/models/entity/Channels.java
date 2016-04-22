package models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "channels")
@Table(name = "channels")
public class Channels extends Model{
    @Id
    @Column(name = "ChannelId", columnDefinition = "int signed not null", nullable = false, unique = true)
    public long channelId = 0;

    @Column(name = "ChannelName", columnDefinition = "varchar(50) not null default 0", nullable = false)
    public String channelName = "";
}
