package com.xjtu.user_subject_list.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.user.domain.User;
import com.xjtu.user.repository.UserRepository;
import com.xjtu.user_domain_list.domain.UserDomainList;
import com.xjtu.user_subject_list.domain.UserSubjectList;
import com.xjtu.user_subject_list.repository.UserSubjectListRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 16:49
 */

@Service
public class UserSubjectListService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserSubjectListRepository userSubjectListRepository;

    @Autowired
    private UserRepository userRepository;

    public Result findSubjectListByUserName(String userName) {
        try {
            User user = userRepository.findByUserName(userName);
            Long userId = user.getUserId();
            UserSubjectList userSubjectList = userSubjectListRepository.findByUserId(userId);
            String subjectList = userSubjectList.getSubjectList();

//            String domainList = userDomainList.getDomainList();
//            String[] seperates = StringUtils.split(domainList, ",");
//            for (int i = 0; i < seperates.length; i++) {
//
//            }

            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), userSubjectList);
        } catch (Exception e) {
            logger.error("用户可见学科查询失败：没有信息记录");
            return ResultUtil.error(ResultEnum.USER_SUBJECT_LIST_ERROR_1.getCode(), ResultEnum.USER_SUBJECT_LIST_ERROR_1.getMsg());
        }
    }


}
