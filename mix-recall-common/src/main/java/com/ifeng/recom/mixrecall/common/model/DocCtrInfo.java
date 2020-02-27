package com.ifeng.recom.mixrecall.common.model;

public class DocCtrInfo {
    private String docId;
    private Double pv;
    private Double ev;
    private Double ctr;
    private Double share;
    private Double store;

    public DocCtrInfo(String docId, Double pv, Double ev, Double ctr, Double share, Double store) {
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

    public Double getPv() {
        return pv;
    }

    public void setPv(Double pv) {
        this.pv = pv;
    }

    public Double getEv() {
        return ev;
    }

    public void setEv(Double ev) {
        this.ev = ev;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public Double getShare() {
        return share;
    }

    public void setShare(Double share) {
        this.share = share;
    }

    public Double getStore() {
        return store;
    }

    public void setStore(Double store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "DocCtrInfo{" +
                "docId='" + docId + '\'' +
                ", pv=" + pv +
                ", ev=" + ev +
                ", ctr=" + ctr +
                ", share=" + share +
                ", store=" + store +
                '}';
    }
}
