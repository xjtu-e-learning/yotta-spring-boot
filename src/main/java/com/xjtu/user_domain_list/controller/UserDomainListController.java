package com.xjtu.user_domain_list.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.service.SubjectService;
import com.xjtu.user_domain_list.service.UserDomainListService;
import com.xjtu.user_subject_list.service.UserSubjectListService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 17:19
 */
@RestController
@RequestMapping(value = "/userDomainList")
public class UserDomainListController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SubjectService subjectService;

    @Autowired
    UserDomainListService userDomainListService;

    @Autowired
    UserSubjectListService userSubjectListService;

    @GetMapping("/getUserDomainListByUserName")
    @ApiOperation(value = "通过用户名获取可见课程列表", notes = "通过用户名获取可见课程列表")
    public ResponseEntity getUserSubjectListByUserName(@RequestParam(name = "userName") String userName,
                                                       @RequestParam(name = "subjectName") String subjectName) {

        Result result = userDomainListService.findDomainListByUserNameAndSubjectName(userName, subjectName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
