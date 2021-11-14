package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "member_nick")
public class MemberNickBean extends BaseBean {

    @Column(name = "qq_number")
    private String qqNumber;

    @Column(name = "nick_name")
    private String nickName;
}
