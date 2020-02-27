package com.ifeng.recom.mixrecall.common.tool;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;

public class IpConvert extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return GyConstant.linuxLocalIp;
    }
}