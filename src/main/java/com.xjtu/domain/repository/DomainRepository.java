package com.xjtu.domain.repository;

import com.xjtu.domain.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DomainRepository extends JpaRepository<Domain, Long>, JpaSpecificationExecutor<Domain>{

    Domain findByDomainName(String domainName);

    Domain findByDomainIdAndDomainName(Long domainId, String domainName);

    List<Domain> findBySubjectId(Long subjectId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Domain d set d.domainId = ?2, d.domainName = ?3, d.subjectId = ?4 where d.domainId = ?1")
    void updateByDomainId(Long domainId, Long newDomainId, String domainName, Long subjectId);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Domain d set  d.domainName = ?2 where d.domainName = ?1")
    void updateDomainByDomainName(String oldDomainName, String newDomainName);

}
