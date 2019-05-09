package com.xjtu.statistics.repository;

import com.xjtu.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author yangkuan
 */

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    /**
     * 根据课程id集合，统计碎片数量
     *
     * @param domainIds
     * @return
     */
    @Query(value = "SELECT domain_id,assemble_number\n" +
            "FROM statistics\n" +
            "WHERE domain_id in ?1", nativeQuery = true)
    List<Object[]> countAssemblesGroupByDomainId(List<Long> domainIds);

}
