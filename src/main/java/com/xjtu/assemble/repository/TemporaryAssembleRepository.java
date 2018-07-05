package com.xjtu.assemble.repository;

import com.xjtu.assemble.domain.TemporaryAssemble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 碎片暂存表，数据库相关操作
 *
 * @author yangkuan
 * @date 2018/04/14 13:47
 */
public interface TemporaryAssembleRepository extends JpaRepository<TemporaryAssemble, Long>, JpaSpecificationExecutor<TemporaryAssemble> {

    /**
     * 根据用户名查询碎片
     *
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<TemporaryAssemble> findByUserName(String userName);

    /**
     * 根据碎片id修改碎片内容
     *
     * @param assembleId      碎片id
     * @param assembleContent 碎片内容
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update TemporaryAssemble t set t.assembleContent = ?2 where t.assembleId = ?1")
    void updateTemporaryAssemble(Long assembleId, String assembleContent);


}
