package com.xjtu.django.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.django.service.PythonService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/django")
public class DjangoController {
    @Autowired
    PythonService pythonService;
    @GetMapping("/killPythonService")
    @ApiOperation(value = "杀死并重启django进程",
            notes = "杀死并重启django进程")
    public ResponseEntity killPythonService(String port){
        Result result=pythonService.killAndRestartPythonService(port);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dependences")
    @ApiOperation(value = "访问django服务/dependences接口，需要自己输入对应服务器上的哪个端口", notes = "访问django服务/dependences接口，需要自己输入对应服务器上的哪个端口")
    public ResponseEntity getDependencesByDomainName(@RequestParam("domainName") String domainName,
                                                         @RequestParam("port")String port){
        Result result=pythonService.getDependencesByDomainName(domainName,port);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}
