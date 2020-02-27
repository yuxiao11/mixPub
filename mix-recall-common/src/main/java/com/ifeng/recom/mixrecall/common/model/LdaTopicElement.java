package com.ifeng.recom.mixrecall.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

@Setter
@Getter
public class LdaTopicElement implements Serializable {

    private String topic;
    private double weight;


    public LdaTopicElement(String topic,  double weight) {
        this.topic = topic;
        this.weight = weight;
    }
}
