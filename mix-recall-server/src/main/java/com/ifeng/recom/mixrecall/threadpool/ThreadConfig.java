package com.ifeng.recom.mixrecall.threadpool;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(150);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("MixRecallAsyncThreadPool-");
//        executor.setTaskDecorator(runnable -> new MonitorTools.MonitorRunnable(runnable));
//        executor.setRejectedExecutionHandler(new MonitorTools.MonitorAbortPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "recallThreadPool")
    public ThreadPoolExecutor recallThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(150);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("recallTP-");
        // 针对通道已满, 不要报错直接关闭任务. 减少wait时间
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof FutureTask) {
                    ((FutureTask) r).cancel(true);
                }
            }
        });
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

    @Bean(name = "recallUserSubThreadPool")
    public ThreadPoolExecutor recallUserSubThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(150);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("recallUserSubTP-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

    @Bean("realTime")
    public Executor getRealTimeAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(150);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("MixRealTimeThreadPool-");
//        executor.setTaskDecorator(runnable -> new MonitorTools.MonitorRunnable(runnable));
//        executor.setRejectedExecutionHandler(new MonitorTools.MonitorAbortPolicy());
        executor.initialize();
        return executor;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
