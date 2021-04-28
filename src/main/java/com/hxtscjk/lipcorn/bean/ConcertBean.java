package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "concert_songlist")
public class ConcertBean extends BaseBean{

    /**
     * 歌曲序号
     */
    @Column(name = "serial")
    private Integer serial;

    /**
     * 演唱者
     */
    @Column(name = "song_singer")
    private String songSinger;

    /**
     * 歌名
     */
    @Column(name = "song_name")
    private String songName;

    /**
     * 演唱会场次
     */
    @Column(name = "concert_session")
    private Integer concertSession;

    /**
     * 演唱会时间
     */
    @Column(name = "concert_date")
    private Date concertDate;

    /**
     * 演唱者首次演唱歌曲标记 1是 0否
     */
    @Column(name = "singer_first")
    private Integer singerFirst;

    /**
     * 首次被演唱歌曲标记 1是 0否
     */
    @Column(name = "song_first")
    private Integer songFirst;

    /**
     * 备注
     */
    @Column(name = "etc")
    private String etc;
}
