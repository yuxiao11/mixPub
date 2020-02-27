package com.ifeng.recom.mixrecall.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/**
 * Created by wupeng1 on 2016/9/26.
 */
public class RecordInfo implements Comparable<RecordInfo> {
    @SerializedName("n")
    @Expose
    private String recordName;

    @SerializedName("c")
    @Expose
    private int readFrequency;

    @SerializedName("s")
    @Expose
    private double weight;

    @SerializedName("e")
    @Expose
    private int expose = -1;

    @SerializedName("sim")
    @Expose
    private double sim;

    public RecordInfo(String recordName, double weight) {
        this.recordName = recordName;
        this.weight = weight;
    }

    public RecordInfo(String recordName, int readFrequency) {
        this.recordName = recordName;
        this.readFrequency = readFrequency;
    }

    public RecordInfo(String recordName, int readFrequency, double weight) {
        this.recordName = recordName;
        this.readFrequency = readFrequency;
        this.weight = weight;
    }

    public RecordInfo(String recordName, int readFrequency, double weight, int expose) {
        this.recordName = recordName;
        this.readFrequency = readFrequency;
        this.weight = weight;
        this.expose = expose;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public int getReadFrequency() {
        return readFrequency;
    }

    public void setReadFrequency(int readFrequency) {
        this.readFrequency = readFrequency;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getExpose() {
        return expose;
    }

    public void setExpose(int expose) {
        this.expose = expose;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    @Override
    public int compareTo(RecordInfo o) {
        if (this.weight > o.weight) {
            return -1;
        } else if (this.weight < o.weight) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "recordName='" + recordName + '\'' +
                ", weight=" + weight +
                '}';
    }

    /**
     * RecordInfo Weight Comparator
     */
    public static class RecordInfoWeightComparator implements Comparator<RecordInfo> {
        @Override
        public int compare(RecordInfo o1, RecordInfo o2) {
            return Double.compare(o2.getWeight(), o1.getWeight());
        }
    }

    public String getDebugContent() {
        return this.recordName + "|" + this.weight;
    }

}
