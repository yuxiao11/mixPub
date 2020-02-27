package com.ifeng.recom.mixrecall.model;

import com.google.gson.annotations.Expose;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;

import java.util.List;

public class RecallChannelResult {
    @Expose(serialize = true)
    private RecallConstant.CHANNEL channel;
    @Expose(serialize = false)
    private List<RecallResult> channelResult;
    @Expose(serialize = true)
    private int size;
    @Expose(serialize = true)
    private int removeSize;
    @Expose(serialize = true)
    private long cost;

    public RecallConstant.CHANNEL getChannel() {
        return channel;
    }

    public RecallChannelResult setChannel(RecallConstant.CHANNEL channel) {
        this.channel = channel;
        return this;
    }

    public List<RecallResult> getChannelResult() {
        return channelResult;
    }

    public RecallChannelResult setChannelResult(List<RecallResult> channelResult) {
        this.channelResult = channelResult;
        if (channelResult != null) {
            this.size = channelResult.size();
        }
        return this;
    }

    public int getSize() {
        return size;
    }

    public RecallChannelResult setSize(int size) {
        this.size = size;
        return this;
    }

    public int getRemoveSize() {
        return removeSize;
    }

    public RecallChannelResult setRemoveSize(int removeSize) {
        this.removeSize = removeSize;
        return this;
    }

    public long getCost() {
        return cost;
    }

    public RecallChannelResult setCost(long cost) {
        this.cost = cost;
        return this;
    }
}
