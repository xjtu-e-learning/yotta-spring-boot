package com.xjtu.education.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.service.CourseWangyuanService;
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
 * @author yangkuan
 */
@RestController
@RequestMapping("/wangyuan")
public class CourseWangyuanController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CourseWangyuanService courseWangyuanService;

    @ApiOperation(value = "根据网院课程id，查询网院课程", notes = "根据网院课程id，查询网院课程")
    @GetMapping("/getDomainByCourseId")
    public ResponseEntity getDomainByCourseId(@RequestParam("courseId") Long courseId) {
        Result result = courseWangyuanService.findDomainByCourseId(courseId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据维基课程id，查询网院课程", notes = "根据维基课程id，查询网院课程")
    @GetMapping("/getDomainByDomainId")
    public ResponseEntity getDomainByDomainId(@RequestParam("domainId") Long domainId) {
        Result result = courseWangyuanService.findDomainByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据维基课程名，查询网院课程", notes = "根据维基课程名，查询网院课程")
    @GetMapping("/getDomainByDomainName")
    public ResponseEntity getDomainByDomainName(@RequestParam("domainName") String domainName) {
        Result result = courseWangyuanService.findDomainByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
