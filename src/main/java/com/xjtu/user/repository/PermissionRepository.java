package com.xjtu.user.repository;


import com.xjtu.domain.domain.Domain;
import com.xjtu.subject.domain.Subject;
import com.xjtu.user.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 带权限控制的课程列表访问
 *
 * @author 张铎
 * @date 2019/06/04
 */

public interface PermissionRepository extends JpaRepository,JpaSpecificationExecutor<Permission> {

    /**
     * 指定用户ID，查询其可访问的学科
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT subject_id \n" +
            "FROM permission \n" +
            "WHERE user_id=?1")
    Subject findSubjectIdByUserId(Long userId);

    /**
     * 指定用户ID，查询其可访问的课程
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT domain_name \n" +
            "FROM permission \n" +
            "WHERE user_id=?1")
    Domain findDomainNameByUserId(Long userId);

}
