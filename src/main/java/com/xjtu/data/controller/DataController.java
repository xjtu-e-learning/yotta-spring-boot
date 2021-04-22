package com.xjtu.data.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.data.service.DataService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data")
public class DataController {
    @Autowired
    private DataService dataService;

    @GetMapping(value = "/assembleTrans")
    @ApiOperation(value = "将complete数据库中的中小学碎片更新到test数据库",notes = "将complete数据库中的中小学碎片更新到test数据库")
    public ResponseEntity assembleTrans(@RequestParam(name = "subjectId") Long subjectId) throws Exception {
        Result result = dataService.assembleTransfer(subjectId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
