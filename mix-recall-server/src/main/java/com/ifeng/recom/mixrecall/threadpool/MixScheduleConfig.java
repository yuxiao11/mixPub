package com.ifeng.recom.mixrecall.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class MixScheduleConfig implements SchedulingConfigurer {

    /**
     * 设置spring schedule线程数, 解决schedule排队问题. 不需要过大.
     *
     * @return
     */
    public ExecutorService schedulerThreadPool() {
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(
                2, new ThreadFactoryBuilder().setNameFormat("spring-scheduler-%d").build());
        return pool;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(schedulerThreadPool());
    }
}
