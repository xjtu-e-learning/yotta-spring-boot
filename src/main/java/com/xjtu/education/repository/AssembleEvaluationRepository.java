package com.xjtu.education.repository;

import com.xjtu.education.domain.AssembleEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yangkuan
 */
public interface AssembleEvaluationRepository extends JpaRepository<AssembleEvaluation, Long> {

    /**
     * 根据碎片名查询用户评价
     *
     * @param assembleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<AssembleEvaluation> findByAssembleId(Long assembleId);


    /**
     * @param assembleId
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    AssembleEvaluation findByAssembleIdAndUserId(Long assembleId, Long domainId);

}
