package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "happy_catch_fish_version")
public class HappyFishVersionBean {

    @Column(name = "id")
    @Id
    private String id;
    /**
     * 上次捞鱼截止的录像id
     */
    @Column(name = "record_id")
    private String recordId;

    /**
     * 上次捞鱼结束时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
