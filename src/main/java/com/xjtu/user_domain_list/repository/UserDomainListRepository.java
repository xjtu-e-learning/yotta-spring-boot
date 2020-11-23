package com.xjtu.user_domain_list.repository;

import com.xjtu.subject.domain.Subject;
import com.xjtu.user.domain.UserLog;
import com.xjtu.user_domain_list.domain.UserDomainList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 10:48
 */
public interface UserDomainListRepository extends JpaRepository<UserDomainList, Long>, JpaSpecificationExecutor<UserDomainList> {
    /**
     * 根据用户Id返回实例
     *
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    UserDomainList findByUserId(Long userId);


    @Transactional(rollbackFor = Exception.class)
    UserDomainList findByUserIdAndSubjectId(Long userId, Long SubjectId);
}
