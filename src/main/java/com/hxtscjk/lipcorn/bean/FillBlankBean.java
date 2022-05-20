package com.hxtscjk.lipcorn.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "fill_blank_times")
public class FillBlankBean {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "qq_number")
    private String qqNumber;

    @Column(name = "times")
    private Integer times;

    @Column(name = "date")
    private String date;
}
