package com.xjtu.user.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.user.service.UserService;
import com.xjtu.utils.HttpUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * api:处理用户登录
 *
 * @author:yangkuan
 * @date:2018/03/14 16:40
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    /**
     * API
     * 数据管理系统，用户登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "数据管理系统，用户登录", notes = "数据管理系统，用户登录")
    public ResponseEntity login(@RequestParam(name = "userName") String userName,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "ip") String ip,
                                @RequestParam(name = "place") String place,
                                @RequestParam(name = "date") String date,
                                HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = userService.login(userName, password, ip, place, date);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
