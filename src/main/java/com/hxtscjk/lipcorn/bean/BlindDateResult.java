package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "blind_date_game_result")
public class BlindDateResult extends BaseBean {

    /**
     * 游戏参加者QQ号
     */
    @Column(name = "qq_number")
    private String qqNumber;

    /**
     * 匹配对象QQ号
     */
    @Column(name = "match_number")
    private String matchNumber;

    /**
     * 匹配日期
     */
    @Column(name = "match_date")
    private String matchDate;

    /**
     * 匹配类型（1-匹配成功 2-调剂成功 3-solo）
     */
    @Column(name = "match_type")
    private String matchType;
}
