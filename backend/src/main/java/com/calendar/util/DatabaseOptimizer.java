package com.calendar.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 数据库查询优化工具
 */
@Slf4j
@Component
public class DatabaseOptimizer {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 批量插入优化
     */
    public <T> void batchInsert(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
                log.debug("Flushed batch at index: {}", i);
            }
        }
        
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 批量更新优化
     */
    public <T> void batchUpdate(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.merge(entities.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
                log.debug("Flushed update batch at index: {}", i);
            }
        }
        
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 执行索引分析
     */
    public void analyzeTable(String tableName) {
        try {
            Query query = entityManager.createNativeQuery("ANALYZE TABLE " + tableName);
            query.executeUpdate();
            log.info("Analyzed table: {}", tableName);
        } catch (Exception e) {
            log.error("Failed to analyze table: {}", tableName, e);
        }
    }

    /**
     * 优化表
     */
    public void optimizeTable(String tableName) {
        try {
            Query query = entityManager.createNativeQuery("OPTIMIZE TABLE " + tableName);
            query.executeUpdate();
            log.info("Optimized table: {}", tableName);
        } catch (Exception e) {
            log.error("Failed to optimize table: {}", tableName, e);
        }
    }

    /**
     * 获取表大小
     */
    public Long getTableSize(String tableName) {
        try {
            Query query = entityManager.createNativeQuery(
                "SELECT data_length + index_length FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = ?"
            );
            query.setParameter(1, tableName);
            Object result = query.getSingleResult();
            return ((Number) result).longValue();
        } catch (Exception e) {
            log.error("Failed to get table size: {}", tableName, e);
            return -1L;
        }
    }

    /**
     * 查询慢查询日志
     */
    public List<Object[]> getSlowQueries(int limit) {
        try {
            Query query = entityManager.createNativeQuery(
                "SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT ?"
            );
            query.setParameter(1, limit);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Failed to get slow queries", e);
            return List.of();
        }
    }
}
