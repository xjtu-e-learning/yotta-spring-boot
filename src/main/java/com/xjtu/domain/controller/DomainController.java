package com.xjtu.domain.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.service.DomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * api:处理domain课程数据
 *
 * @author:yangkuan
 * @date:2018/03/08 14:35
 */
@RestController
@RequestMapping(value = "/domain")
public class DomainController {

    @Autowired
    private DomainService domainService;


    @GetMapping("/getDomainsGroupBySubject")
    @ApiOperation(value = "获得学科和课程信息，不包含主题信息", notes = "获得学科和课程信息，不包含主题信息")
    public ResponseEntity getDomainsGroupBySubject() {
        Result result = domainService.findDomainsGroupBySubject();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getDomainsBySubject")
    @ApiOperation(value = "根据学科名，返回该学科下的所有课程", notes = "根据学科名，返回该学科下的所有课程")
    public ResponseEntity getDomainsBySubject(@RequestParam(name = "subjectName") String subjectName) {
        Result result = domainService.findDomainsBySubject(subjectName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getDomains")
    @ApiOperation(value = "获得所有课程信息", notes = "获得所有课程信息")
    public ResponseEntity getDomains() {
        Result result = domainService.findDomains();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getDomainById")
    @ApiOperation(value = "根据课程id，获得课程信息", notes = "根据课程id，获得课程信息")
    public ResponseEntity getDomainById(@RequestParam(name = "domainId") Long domainId) {
        Result result = domainService.findDomainById(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getDomainByName")
    @ApiOperation(value = "根据课程名，获得课程信息", notes = "根据课程名，获得课程信息")
    public ResponseEntity getDomainByName(@RequestParam(name = "domainName") String domainName) {
        Result result = domainService.findDomainByName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getDomainStatisticalChartByDomainName")
    @ApiOperation(value = "统计课程数据（包括主题、主题依赖关系、分面、碎片）", notes = "统计课程数据（包括主题、主题依赖关系、分面、碎片）")
    public ResponseEntity getDomainStatisticalChartByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = domainService.findDomainStatisticalChartByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 读取domain，得到所有领域名和各领域下主题、分面、碎片、关系的数量
     * 未写
     */


    @ApiOperation(value = "根据课程名，查询该课程下面主题，以及分面按树状组织", notes = "根据课程名，查询该课程下面主题，以及分面按树状组织")
    @GetMapping("/getDomainTreeByDomainName")
    public ResponseEntity getDomainTreeByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = domainService.findDomainTreeByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 插入一门课程
     * insertDomain
     */
    @GetMapping("/insertDomain")
    @ApiOperation(value = "插入一门课程", notes = "插入一门课程")
    public ResponseEntity insertDomain(@RequestParam(name = "domainName") String domainName) {
        Result result = domainService.insertDomainByName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/countDomains")
    @ApiOperation(value = "统计课程数量", notes = "统计课程数量")
    public ResponseEntity countDomains() {
        Result result = domainService.countDomains();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/updateDomainByDomainName")
    @ApiOperation(value = "更新课程名", notes = "更新课程名")
    public ResponseEntity updateDomainByDomainName(@RequestParam(name = "oldDomainName") String oldDomainName
            , @RequestParam(name = "newDomainName") String newDomainName) {
        Result result = domainService.updateDomainByDomainName(oldDomainName, newDomainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 按课程转换RDF数据
     * 未写
     */


}
