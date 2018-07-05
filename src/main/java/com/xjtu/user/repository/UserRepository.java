package com.xjtu.user.repository;

import com.xjtu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户登录信息数据的数据库操作
 *
 * @author yangkuan
 * @date 2018/03/14 11:17
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 指定用户名、密码，查询用户信息
     *
     * @param userName 用户名
     * @param password 密码
     * @return User
     */
    @Transactional(rollbackFor = Exception.class)
    User findByUserNameAndPassword(String userName, String password);


}
