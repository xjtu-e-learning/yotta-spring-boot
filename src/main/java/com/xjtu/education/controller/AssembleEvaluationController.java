package com.xjtu.education.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.service.AssembleEvaluationService;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/evaluation")
public class AssembleEvaluationController {

    @Autowired
    AssembleEvaluationService assembleEvaluationService;

    @ApiOperation(value = "保存碎片评价", notes = "保存碎片评价")
    @GetMapping("/saveAssembleEvaluation")
    public ResponseEntity saveAssembleEvaluation(@RequestParam(name = "userId") Long userId,
                                              @RequestParam(name = "assembleId") Long assembleId,
                                              @RequestParam(name = "value") Integer value) {
        Result result = assembleEvaluationService.saveAssembleEvaluation(userId, assembleId, value);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "查询碎片评价", notes = "查询碎片评价")
    @GetMapping("/getAssembleEvaluationStatistics")
    public ResponseEntity findAssembleEvaluationStatistics(@RequestParam("assembleId") Long assembleId) {
        Result result = assembleEvaluationService.findAssembleEvaluationStatistics(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
