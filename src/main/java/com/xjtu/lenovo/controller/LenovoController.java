package com.xjtu.lenovo.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.lenovo.service.LenovoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lenovo")
public class LenovoController {

    @Autowired
    private LenovoService lenovoService;

    @ApiOperation(value = "联想知识点与主题和分面匹配情况",notes="联想知识点与主题和分面匹配情况")
    @GetMapping("/find")
    public ResponseEntity find(){
        Result result=lenovoService.findKgInTopicAndFacet();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "联想知识点与主题和分面匹配情况",notes="联想知识点与主题和分面匹配情况")
    @GetMapping("/find1")
    public ResponseEntity find1(){
        Result result=lenovoService.findKgInTopicAndFacet1();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
