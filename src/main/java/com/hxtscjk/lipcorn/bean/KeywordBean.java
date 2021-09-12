package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "keyword")
public class KeywordBean {

    /**
     * 匹配类型 1-完整匹配 2-模糊匹配 3-正则
     */
    @Column(name = "match_type")
    private Integer matchType;

    /**
     * 回复类型 1-字符串 2-功能
     */
    @Column(name = "reply_type")
    private Integer replyType;
    /**
     * 关键词
     */
    private String keyword;

    /**
     * 回复 仅对于字符串回复
     */
    private String reply;

    /**
     * 优先级 100最高 1最低
     */
    private String level;
}
