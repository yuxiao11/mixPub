package com.ifeng.recom.mixrecall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 监控用的工具类
 */
public class MonitorTools {

    public static final Logger logger = LoggerFactory.getLogger(MonitorTools.class);

    public static final double milli = 1000000.0D;

    public static Time newMonitorTime() {
        return new Time();
    }

    public static class Time {
        long start = System.nanoTime();
        long n = start;

        private Time() {
        }

        public long allTime() {
            return System.nanoTime() - start;
        }

        public long beforeInterval() {
            long t = n;
            n = System.nanoTime();
            return n - t;
        }

        public double allTimeMilli() {
            return allTime() / milli;
        }

        public double beforeIntervalMilli() {
            return beforeInterval() / milli;
        }
    }

    public static class MonitorDiscardOldestPolicy extends ThreadPoolExecutor.DiscardOldestPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                Runnable old = e.getQueue().poll();
                if (old != null) {
                    if (old instanceof ThreadMonitor) {
                        try {
                            ((ThreadMonitor) old).discard();
                            ((ThreadMonitor) old).monitorLogger(0, 0);
                        } catch (Exception ex) {
                        }
                    }
                }
                e.execute(r);
            }
        }
    }

    public static class MonitorAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof ThreadMonitor) {
                try {
                    ((ThreadMonitor) r).discard();
                    ((ThreadMonitor) r).monitorLogger(0, 0);
                } catch (Exception ex) {
                }
            }
            super.rejectedExecution(r, executor);
        }
    }

    public interface ThreadMonitor {
        void discard();

        void monitorLogger(double waitTime, double runtime);
    }

    public static class MonitorCallable<V> implements Callable<V>, ThreadMonitor {
        MonitorTools.Time tool = MonitorTools.newMonitorTime();
        final static AtomicInteger concurrent = new AtomicInteger(0);
        int concurrentCount;

        public MonitorCallable(Callable<V> runnable) {
            this.runnable = runnable;
            concurrentCount = concurrent.incrementAndGet();
        }

        Callable<V> runnable;

        @Override
        public void discard() {
            concurrent.decrementAndGet();
        }

        @Override
        public void monitorLogger(double waitTime, double runtime) {
            MonitorTools.logger.info("execute runtime:{} , waitTime:{}, totalTime:{}, concurrent:{}",
                    runtime, waitTime, waitTime + runtime, concurrentCount);
        }

        @Override
        public V call() throws Exception {
            double waitTime = tool.beforeIntervalMilli();
            try {
                return runnable.call();
            } finally {
                concurrent.decrementAndGet();
                try {
                    double runtime = tool.beforeIntervalMilli();
                    monitorLogger(waitTime, runtime);
                } catch (Exception e) {
                }
            }
        }
    }


    public static class MonitorRunnable implements Runnable, ThreadMonitor {
        MonitorTools.Time tool = MonitorTools.newMonitorTime();
        final static AtomicInteger concurrent = new AtomicInteger(0);
        int concurrentCount;

        public MonitorRunnable(Runnable runnable) {
            this.runnable = runnable;
            concurrentCount = concurrent.incrementAndGet();
        }

        Runnable runnable;

        @Override
        public void run() {
            double waitTime = tool.beforeIntervalMilli();
            try {
                runnable.run();
            } finally {
                concurrent.decrementAndGet();
                try {
                    double runtime = tool.beforeIntervalMilli();
                    monitorLogger(waitTime, runtime);
                } catch (Exception e) {
                }
            }
        }

        @Override
        public void discard() {
            concurrent.decrementAndGet();
        }

        @Override
        public void monitorLogger(double waitTime, double runtime) {
            MonitorTools.logger.info("execute runtime:{} , waitTime:{}, totalTime:{}, concurrent:{}",
                    runtime, waitTime, waitTime + runtime, concurrentCount);
        }
    }

//    public static final String guavaCacheStatus(CacheStats stats, long size) {
//        return String.format("hit_count:%d, hit_rate:%5.2f, load_count:%d, " +
//                        "loadSuccessCount:%d, loadExceptionCount:%d, loadExceptionRate:%5.2f, " +
//                        "missCount:%d, missRate:%5.2f, cache_size:%d",
//                stats.hitCount(), stats.hitRate(), stats.loadCount(),
//                stats.loadSuccessCount(), stats.loadExceptionCount(), stats.loadExceptionRate(),
//                stats.missCount(), stats.missRate(), size);
//    }
}
