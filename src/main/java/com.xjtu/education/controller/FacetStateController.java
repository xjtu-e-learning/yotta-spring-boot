package com.xjtu.education.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.service.FacetStateService;
import com.xjtu.utils.HttpUtil;
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

import javax.servlet.http.HttpServletRequest;

/**
 * @author yangkuan
 */
@RestController
@RequestMapping("/facetState")
public class FacetStateController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    FacetStateService facetStateService;

    @GetMapping("/saveStateByDomainIdAndUserId")
    @ApiOperation(value = "保存分面状态", notes = "保存分面状态")
    public ResponseEntity saveState(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "states") String states
            , @RequestParam(name = "userId") Long userId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = facetStateService.saveState(domainId, states, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/saveStateByDomainNameAndUserId")
    @ApiOperation(value = "保存分面状态", notes = "保存分面状态")
    public ResponseEntity saveState(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "states") String states
            , @RequestParam(name = "userId") Long userId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = facetStateService.saveState(domainName, states, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getByDomainIdAndUserId")
    @ApiOperation(value = "查询分面状态", notes = "查询分面状态")
    public ResponseEntity getByDomainIdAndUserId(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "userId") Long userId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = facetStateService.findByDomainIdAndUserId(domainId, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
