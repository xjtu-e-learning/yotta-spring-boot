package com.xjtu.source.repository;

import com.xjtu.source.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yuanhao
 * @date 2018/3/4 16:58
 */
public interface SourceRepository extends JpaRepository<Source, Long>, JpaSpecificationExecutor<Source> {

    /**
     * 根据数据源id，查询数据源
     *
     * @param sourceId
     * @return 数据源
     */
    @Transactional(rollbackFor = Exception.class)
    Source findBySourceId(Long sourceId);

    /**
     * 根据数据源id和数据源名，查询数据源
     *
     * @param sourceId
     * @param sourceName
     * @return 数据源
     */
    @Transactional(rollbackFor = Exception.class)
    Source findBySourceIdAndSourceName(Long sourceId, String sourceName);

    /**
     * 根据数据源名，查询数据源
     *
     * @param sourceName
     * @return
     */
    Source findBySourceName(String sourceName);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Source s set s.sourceId = ?2, s.sourceName = ?3, s.note = ?4 where s.sourceId = ?1")
    void updateBySourceId(Long sourceId, Long newSourceId, String sourceName, String note);

}
