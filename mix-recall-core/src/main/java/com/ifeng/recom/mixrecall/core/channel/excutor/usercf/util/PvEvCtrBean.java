package com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util;

import java.util.List;

public class PvEvCtrBean {
    private String docId;
    private List<Double> pv;
    private List<Double> ev;
    private List<Double> ctr;
    private List<Double> share;
    private List<Double> store;

    public PvEvCtrBean(String docId, List<Double> pv, List<Double> ev, List<Double> ctr, List<Double> share, List<Double> store) {
        super();
        this.docId = docId;
        this.pv = pv;
        this.ev = ev;
        this.ctr = ctr;
        this.share = share;
        this.store = store;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public List<Double> getPv() {
        return pv;
    }

    public void setPv(List<Double> pv) {
        this.pv = pv;
    }

    public List<Double> getEv() {
        return ev;
    }

    public void setEv(List<Double> ev) {
        this.ev = ev;
    }

    public List<Double> getCtr() {
        return ctr;
    }

    public void setCtr(List<Double> ctr) {
        this.ctr = ctr;
    }

    public List<Double> getShare() {
        return share;
    }

    public void setShare(List<Double> share) {
        this.share = share;
    }

    public List<Double> getStore() {
        return store;
    }

    public void setStore(List<Double> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "PvEvCtrBean{" +
                "docId='" + docId + '\'' +
                ", pv=" + pv +
                ", ev=" + ev +
                ", ctr=" + ctr +
                ", share=" + share +
                ", store=" + store +
                '}';
    }
}
