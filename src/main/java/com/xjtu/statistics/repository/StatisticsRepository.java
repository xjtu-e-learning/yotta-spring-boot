package com.xjtu.statistics.repository;

import com.xjtu.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yangkuan
 */

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

}
