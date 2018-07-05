package com.xjtu.question.repository;

import com.xjtu.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 问题质量的相关数据库操作
 *
 * @author yangkuan
 * @date 2018/05/19
 */
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    /**
     * 根据碎片id，查询所有问题碎片
     *
     * @param assembleId 碎片id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Question findByAssembleId(Long assembleId);

    /**
     * 更新问题中的提问者信息
     *
     * @param askerName
     * @param askerReputation
     * @param askerAnswerCount
     * @param askerQuestionCount
     * @param askerViewCount
     * @param questionId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Question q " +
            "set q.askerName = ?1, " +
            "q.askerReputation = ?2, " +
            "q.askerAnswerCount = ?3, " +
            "q.askerQuestionCount = ?4, " +
            "q.askerViewCount = ?5 " +
            "where q.questionId = ?6")
    void updateByQuestionId(String askerName
            , String askerReputation
            , String askerAnswerCount
            , String askerQuestionCount
            , String askerViewCount
            , Long questionId);

    /**
     * 根据数据源和课程名，查询问题碎片
     *
     * @param sourceName
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select q " +
            "from Question q, " +
            "Assemble a, " +
            "Facet f, " +
            "Topic t, " +
            "Domain d, " +
            "Source s " +
            "where s.sourceName = ?1 and " +
            "d.domainName = ?2 and " +
            "q.assembleId = a.assembleId and " +
            "a.facetId = f.facetId and " +
            "f.topicId = t.topicId and " +
            "t.domainId = d.domainId")
    List<Question> findBySourceNameAndDomainName(String sourceName, String domainName);


}
