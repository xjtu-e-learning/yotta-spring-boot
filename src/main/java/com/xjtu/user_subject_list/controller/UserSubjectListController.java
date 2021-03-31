package com.xjtu.user_subject_list.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.user.service.UserService;
import com.xjtu.user_subject_list.service.UserSubjectListService;
import com.xjtu.utils.HttpUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 17:08
 */
@RestController
@RequestMapping(value = "/userSubjectList")
public class UserSubjectListController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserSubjectListService userSubjectListService;

    @GetMapping("/getUserSubjectListByUserName")
    @ApiOperation(value = "通过用户名获取可见学科列表", notes = "通过用户名获取可见学科列表")
    public ResponseEntity getUserSubjectListByUserName(@RequestParam(name = "userName") String userName) {

        Result result = userSubjectListService.findSubjectListByUserName(userName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
