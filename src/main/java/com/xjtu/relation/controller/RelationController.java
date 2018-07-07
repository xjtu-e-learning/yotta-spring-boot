package com.xjtu.relation.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.relation.service.RelationService;
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
 * 关于主题上下位关系的API
 *
 * @author yangkuan
 * @date 2018/03/17 21:08
 */

@RestController
@RequestMapping("/relation")
public class RelationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RelationService relationService;

    @GetMapping(value = "/getHyponymyRelationByDomainName")
    @ApiOperation(value = "通过课程名，查询上下位关系", notes = "通过课程名，查询上下位关系")
    public ResponseEntity getHyponymyRelationByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = relationService.findHyponymyRelationByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
