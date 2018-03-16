package com.xjtu.assemble.repository;


import com.xjtu.assemble.domain.Assemble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 碎片表数据库操作
 * @author yangkuan
 * @date 2018/03/15 14:47
 * */
public interface AssembleRepository extends JpaRepository<Assemble, Long>,JpaSpecificationExecutor<Assemble> {
    /**
     *指定分面id，获取对应分面下的碎片
     * @param facetId 分面id
     * @return List<Assemble>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Assemble> findByFacetId(Long facetId);


}
