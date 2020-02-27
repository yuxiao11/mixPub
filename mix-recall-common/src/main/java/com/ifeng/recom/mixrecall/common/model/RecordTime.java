package com.ifeng.recom.mixrecall.common.model;

import lombok.Getter;
import lombok.Setter;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Created by lilg1 on 2018/1/19.
 */
@Getter
@Setter
public class RecordTime {
    private String recordName;
    private Timestamp timestamp;
    private final static Logger logger = LoggerFactory.getLogger(RecordTime.class);

    public RecordTime(String recordName, String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.recordName = recordName;
        try {
            if (NumberUtils.isNumber(timestamp)) {
                timestamp = timestamp + "000";
                Long ts = Long.valueOf(timestamp);
                this.timestamp = new Timestamp(ts);
            } else {
                if(timestamp.contains("+")){
                    //时间戳替换
                    timestamp = timestamp.replace("+"," ");
                }
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
                this.timestamp = Timestamp.valueOf(dateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Parse timestamp error {}", e);
            logger.error("time:" + recordName + " " + timestamp);
            this.timestamp = new Timestamp(System.currentTimeMillis());
        }
    }

    public static class TimeComparator implements Comparator<RecordTime> {

        @Override
        public int compare(RecordTime o1, RecordTime o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    public static void main(String[] args){
        RecordTime record = new RecordTime("测试","2018-01-29 11:24:04");
        System.out.println(record.getRecordName());
        System.out.println(record.getTimestamp());
    }
}
