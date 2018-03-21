package com.xjtu.statistics.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.statistics.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API数据统计
 * @author yangkuan
 * @date 2018/03/21 19:09
 * */
@RequestMapping("/statistics")
@RestController
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    /**
     * API
     * 根据课程名，查询该课程下的统计数据
     * */
    @GetMapping("/getStatisticalInformationByDomainName")
    @ApiOperation(value = "根据课程名，查询该课程下的统计数据", notes = "根据课程名，查询该课程下的统计数据")
    public ResponseEntity getStatisticalInformationByDomainName(@RequestParam(name = "domainName") String domainName){
        Result result = statisticsService.findStatisticalInformationByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
