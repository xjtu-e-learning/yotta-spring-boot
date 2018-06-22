package com.xjtu.education.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.service.StateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主题状态接口
 *
 * @author yangkuan
 */
@RestController
@RequestMapping("/state")
public class StateController {

    @Autowired
    StateService stateService;

    @GetMapping("/saveStateByDomainIdAndUserId")
    @ApiOperation(value = "保存主题状态", notes = "保存主题状态")
    public ResponseEntity saveState(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "states") String states
            , @RequestParam(name = "userId") Long userId) {
        Result result = stateService.saveState(domainId, states, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/saveStateByDomainNameAndUserId")
    @ApiOperation(value = "保存主题状态", notes = "保存主题状态")
    public ResponseEntity saveState(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "states") String states
            , @RequestParam(name = "userId") Long userId) {
        Result result = stateService.saveState(domainName, states, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/getByDomainIdAndUserId")
    @ApiOperation(value = "查询主题状态", notes = "查询主题状态")
    public ResponseEntity getByDomainIdAndUserId(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "userId") Long userId) {
        Result result = stateService.findByDomainIdAndUserId(domainId, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
