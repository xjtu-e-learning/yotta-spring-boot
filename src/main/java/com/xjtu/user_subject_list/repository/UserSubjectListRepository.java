package com.xjtu.user_subject_list.repository;

import com.xjtu.user_subject_list.domain.UserSubjectList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 16:45
 */
public interface UserSubjectListRepository extends JpaRepository<UserSubjectList, Long>, JpaSpecificationExecutor<UserSubjectList> {
    /**
     * 根据用户Id返回实例
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    UserSubjectList findByUserId(Long userId);
}
