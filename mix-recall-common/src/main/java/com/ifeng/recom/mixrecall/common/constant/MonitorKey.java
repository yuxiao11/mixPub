package com.ifeng.recom.mixrecall.common.constant;

public enum MonitorKey {
    SIZE("数量", Long.class),
    COST("耗时", Long.class),
    RECOM_COST("doRecom步骤耗时", Long.class),
    UID("uid", String.class),
    CHANNEL_SIZE("通道召回量统计", Long.class) {
        @Override
        public String strMapper(String str) {
            return "CH_SZ_" + str;
        }
    },
    CHANNEL_COST("通道耗时", Long.class) {
        @Override
        public String strMapper(String str) {
            return "CH_COST_" + str;
        }
    },
    ;
    /**
     * 注释
     */
    private final String content;
    /**
     * 字段类型, 当前无法做编译检查人工检查
     */
    private final Class clazz;

    MonitorKey(String content, Class clazz) {
        this.content = content;
        this.clazz = clazz;
    }

    public String strMapper(String str) {
        return name();
    }
}
