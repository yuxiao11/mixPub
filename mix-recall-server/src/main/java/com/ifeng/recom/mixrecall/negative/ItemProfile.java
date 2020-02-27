package com.ifeng.recom.mixrecall.negative;

import com.ifeng.recom.mixrecall.common.model.Document;

import java.io.Serializable;

/**
 * Created by jibin on 2017/5/12.
 */
public class ItemProfile implements Serializable {


    private static final long serialVersionUID = -406817482341953203L;
    private String type;


//    private HeadLineItemProfile headLineItemProfile;

    private Document document;

    public ItemProfile(){

    }

    public ItemProfile(String type) {
        this.type = type;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
