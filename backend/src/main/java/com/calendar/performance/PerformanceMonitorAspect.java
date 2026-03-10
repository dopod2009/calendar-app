package com.calendar.performance;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控切面
 */
@Slf4j
@Aspect
@Component
public class PerformanceMonitorAspect {

    private static final long SLOW_THRESHOLD_MS = 1000L;
    
    private final Map<String, PerformanceStats> statsMap = new ConcurrentHashMap<>();

    /**
     * 监控Controller方法执行时间
     */
    @Around("execution(* com.calendar.controller.*.*(..))")
    public Object monitorController(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitor(joinPoint, "Controller");
    }

    /**
     * 监控Service方法执行时间
     */
    @Around("execution(* com.calendar.service.*.*(..))")
    public Object monitorService(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitor(joinPoint, "Service");
    }

    /**
     * 监控Repository方法执行时间
     */
    @Around("execution(* com.calendar.repository.*.*(..))")
    public Object monitorRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitor(joinPoint, "Repository");
    }

    /**
     * 执行监控
     */
    private Object monitor(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String key = layer + "." + className + "." + methodName;

        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            recordPerformance(key, duration, true);
            
            if (duration > SLOW_THRESHOLD_MS) {
                log.warn("⚠️ SLOW {} method: {} took {}ms", layer, key, duration);
            } else {
                log.debug("✓ {} method: {} completed in {}ms", layer, key, duration);
            }
            
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            recordPerformance(key, duration, false);
            log.error("✗ {} method: {} failed after {}ms", layer, key, duration, e);
            throw e;
        }
    }

    /**
     * 记录性能数据
     */
    private void recordPerformance(String key, long duration, boolean success) {
        statsMap.compute(key, (k, stats) -> {
            if (stats == null) {
                stats = new PerformanceStats(k);
            }
            stats.record(duration, success);
            return stats;
        });
    }

    /**
     * 获取性能报告
     */
    public Map<String, PerformanceStats> getPerformanceReport() {
        return new ConcurrentHashMap<>(statsMap);
    }

    /**
     * 清除统计数据
     */
    public void clearStats() {
        statsMap.clear();
    }

    /**
     * 性能统计数据
     */
    public static class PerformanceStats {
        private final String operation;
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxTime = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong failureCount = new AtomicLong(0);

        public PerformanceStats(String operation) {
            this.operation = operation;
        }

        public void record(long duration, boolean success) {
            count.incrementAndGet();
            totalTime.addAndGet(duration);
            
            long currentMin;
            do {
                currentMin = minTime.get();
                if (duration >= currentMin) break;
            } while (!minTime.compareAndSet(currentMin, duration));
            
            long currentMax;
            do {
                currentMax = maxTime.get();
                if (duration <= currentMax) break;
            } while (!maxTime.compareAndSet(currentMax, duration));
            
            if (success) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }
        }

        public double getAverageTime() {
            long total = totalTime.get();
            long cnt = count.get();
            return cnt > 0 ? (double) total / cnt : 0.0;
        }

        // Getters
        public String getOperation() { return operation; }
        public long getCount() { return count.get(); }
        public long getTotalTime() { return totalTime.get(); }
        public long getMinTime() { return minTime.get(); }
        public long getMaxTime() { return maxTime.get(); }
        public long getSuccessCount() { return successCount.get(); }
        public long getFailureCount() { return failureCount.get(); }
    }
}
