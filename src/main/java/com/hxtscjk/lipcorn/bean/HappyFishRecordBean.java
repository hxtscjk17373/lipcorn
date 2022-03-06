package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "happy_catch_fish_record")
public class HappyFishRecordBean extends BaseBean {

    /**
     * 雷网id
     */
    @Column(name = "player_id")
    private String playerId;

    /**
     * 姓名
     */
    @Column(name = "player_name")
    private String playerName;

    /**
     * 此局纪录的time
     */
    @Column(name = "record_time")
    private Double recordTime;

    /**
     * 玩家状态标记（0-未发送待确认 1-已发送 2-已进群 99-黑名单）
     */
    @Column(name = "player_status")
    private Integer playerStatus;

    /**
     * 已发送捞鱼信息次数
     */
    @Column(name = "send_count")
    private Integer sendCount;

    /**
     * 上次发送消息时间
     */
    @Column(name = "last_send")
    private Date lastSend;
}
