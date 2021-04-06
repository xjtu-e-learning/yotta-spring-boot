package com.xjtu.spider_new.repository;

import com.xjtu.spider_new.domain.MissingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MissingRecordRepository extends JpaRepository<MissingRecord, Long>, JpaSpecificationExecutor<MissingRecord> {

    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteById(Long id);

    @Transactional(rollbackFor = Exception.class)
    Optional<MissingRecord> findFirstBy();


    @Transactional(rollbackFor = Exception.class)
    Optional<MissingRecord> findFirstByType(int type);

    @Transactional(rollbackFor = Exception.class)
    int countByType(int type);

    @Transactional(rollbackFor = Exception.class)
    MissingRecord findById(Long id);
}
