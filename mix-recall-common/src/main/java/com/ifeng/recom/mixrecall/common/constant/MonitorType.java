package com.ifeng.recom.mixrecall.common.constant;

public enum MonitorType {
    RECALL_BASIC("召回基础type"),
    INCR("增量召回"),
    CHANNEL_SIZE("通道召回数量"),
    CHANNEL_COST("通道召回耗时"),
    RECALL_NAME("召回名称") {
        @Override
        public String strMapper(String str) {
            return "RE_NAME_" + str;
        }
    },

    ;
    final String content;

    MonitorType(String content) {
        this.content = content;
    }

    public String strMapper(String str) {
        return name();
    }
}
