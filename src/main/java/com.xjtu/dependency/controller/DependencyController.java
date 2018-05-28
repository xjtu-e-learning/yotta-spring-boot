package com.xjtu.dependency.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.service.DependencyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.*;

/**
 *api:处理主题依赖关系
 *@author:yangkuan
 *@date:2018/03/21 13:19
 */
@RestController
@RequestMapping(value = "dependency")
public class DependencyController {

    @Autowired
    DependencyService dependencyService;


    @PostMapping("/insertDependency")
    @ApiOperation(value = "通过主课程名，在课程下的插入、添加主题依赖关系", notes = "通过主课程名，在课程下的插入、添加主题依赖关系")
    public ResponseEntity insertDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicName") String startTopicName
            , @RequestParam(name = "endTopicName") String endTopicName){
        Result result = dependencyService.insertDependency(domainName,startTopicName,endTopicName);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependency")
    @ApiOperation(value = "通过主课程名，起始、终止主题id删除依赖关系", notes = "通过主课程名，起始、终止主题id删除依赖关系")
    public ResponseEntity deleteDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicId") Long startTopicId
            , @RequestParam(name = "endTopicId") Long endTopicId){
        Result result = dependencyService.deleteDependency(domainName,startTopicId,endTopicId);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     * */
    @GetMapping("/getDependenciesByKeyword")
    @ApiOperation(value = "通过关键词，获取该课程下的主题依赖关系", notes = "通过关键词，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByKeyword(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "keyword") String keyword){
        Result result = dependencyService.findDependenciesByKeyword(domainName,keyword);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过主课程名，获取该课程下的主题依赖关系
     * */
    @GetMapping("/getDependenciesByDomainName")
    @ApiOperation(value = "通过主课程名，获取该课程下的主题依赖关系", notes = "通过主课程名，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByDomainName(@RequestParam(name = "domainName") String domainName){
        Result result = dependencyService.findDependenciesByDomainName(domainName);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * API
     * 通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件
     * */
    @PostMapping("/getDependenciesByDomainNameSaveAsGexf")
    @ApiOperation(value = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件"
            , notes = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件")
    public ResponseEntity getDependenciesByDomainNameSaveAsGexf(@RequestParam(name = "domainName") String domainName){
        Result result = dependencyService.findDependenciesByDomainNameSaveAsGexf(domainName);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
