package com.xjtu.domain.repository;

import com.xjtu.user.domain.Permission;
import com.xjtu.domain.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface DomainRepository extends JpaRepository<Domain, Long>, JpaSpecificationExecutor<Domain> {

    /**
     * 根据课程名查询课程
     *
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Domain findByDomainName(String domainName);

    /**
     * 根据课程id和课程名查询课程
     *
     * @param domainId
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Domain findByDomainIdAndDomainName(Long domainId, String domainName);

    /**
     * 根据学科id查询学科所属课程
     *
     * @param subjectId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<Domain> findBySubjectId(Long subjectId);

    /**
     * 根据课程id更新课程
     *
     * @param domainId
     * @param newDomainId
     * @param domainName
     * @param subjectId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Domain d set d.domainId = ?2, d.domainName = ?3, d.subjectId = ?4 where d.domainId = ?1")
    void updateByDomainId(Long domainId, Long newDomainId, String domainName, Long subjectId);

    /**
     * 根据课程名，更新课程数据
     *
     * @param oldDomainName
     * @param newDomainName
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Domain d set  d.domainName = ?2 where d.domainName = ?1")
    void updateDomainByDomainName(String oldDomainName, String newDomainName);

    /**
     * 根据关键字，查询相关课程
     *
     * @param keyword
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select d from Domain d where d.domainName like %?1%")
    List<Domain> findByKeyword(String keyword);

}
