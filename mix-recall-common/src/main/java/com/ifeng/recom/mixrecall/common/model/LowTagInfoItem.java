package com.ifeng.recom.mixrecall.common.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;


@Getter
@Setter
public class LowTagInfoItem {

    private String recomId;

    private String auditTags;

    private Date insertTime;
}
