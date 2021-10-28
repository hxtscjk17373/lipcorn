package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "blind_date_game")
public class BlindDateBean extends BaseBean {

    /**
     * 游戏参加者QQ号
     */
    @Column(name = "qq_number")
    private String qqNumber;

    /**
     * 性别
     */
    @Column(name = "sex")
    private String sex;

    /**
     * 取向
     */
    @Column(name = "orientation")
    private String orientation;

    /**
     * 演唱会场次
     */
    @Column(name = "obey_adjust")
    private String obeyAdjust;

}

