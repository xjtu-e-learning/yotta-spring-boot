package com.xjtu.user_domain_list.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.user.repository.UserRepository;
import com.xjtu.user_domain_list.domain.UserDomainList;
import com.xjtu.user_domain_list.repository.UserDomainListRepository;
import com.xjtu.utils.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 11:20
 */

@Service
public class UserDomainListService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDomainListRepository userDomainListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public Result findDomainListByUserNameAndSubjectName(String userName, String subjectName) {
        try {
            Long subjectId = subjectRepository.findBySubjectName(subjectName).getSubjectId();
            Long userId = userRepository.findByUserName(userName).getUserId();
            UserDomainList userDomainList = userDomainListRepository.findByUserIdAndSubjectId(userId, subjectId);
            String domainList = userDomainList.getDomainList();
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainList);
        } catch (Exception e) {
            logger.error("用户可见课程查询失败：没有信息记录");
            return ResultUtil.error(ResultEnum.USER_DOMAIN_LIST_ERROR_1.getCode(), ResultEnum.USER_DOMAIN_LIST_ERROR_1.getMsg());
        }
    }


}
