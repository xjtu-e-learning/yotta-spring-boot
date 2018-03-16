package com.xjtu.topic.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.topic.service.TopicService;
import io.swagger.annotations.ApiOperation;
import org.apache.regexp.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *api:处理topic主题数据
 *@author:yangkuan
 *@date:2018/03/09 16:40
 */
@RestController
@RequestMapping(value = "/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;


    /**
     * API
     * 添加一个主题
     * 在选定的课程下添加新主题
     * */
    @GetMapping("/insertTopicByNameAndDomainName")
    @ApiOperation(value = "插入主题信息，指定插入课程名和主题名", notes = "插入主题信息，指定插入课程名和主题名")
    public ResponseEntity insertTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName){
        Result result = topicService.insertTopicByNameAndDomainName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    /**
     * API
     * 删除主题
     * 根据课程名和主题名进行删除
     * */
    @GetMapping("/deleteDomainByNameAndTopicName")
    @ApiOperation(value = "删除主题", notes = "根据课程名和主题名进行删除")
    public ResponseEntity deleteDomainByNameAndTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName){
        Result result = topicService.deleteDomainByNameAndTopicName(topicName, domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }




    /**
     * API
     * 更新主题名
     * 根据旧主题名进行更新
     * */
    @GetMapping("/updateTopicByTopicName")
    @ApiOperation(value = "更新主题名", notes = "根据旧主题名进行更新")
    public ResponseEntity updateTopicByTopicName(@RequestParam(name = "oldTopicName") String oldTopicName
            , @RequestParam(name = "newTopicName") String newTopicName
            , @RequestParam(name = "newDomainName") String newDomainName){
        Result result = topicService.updateTopicByName(oldTopicName, newTopicName, newDomainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getTopicsByDomainName")
    @ApiOperation(value = "获得主题信息", notes = "输入课程名，获得课程下主题信息")
    public ResponseEntity findTopicsByDomainName(@RequestParam(name = "domainName") String domainName){
        Result result = topicService.findTopicsByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 获得指定课程、指定主题的所有信息
     * 获得指定课程下指定主题的所有信息
     * */
    @GetMapping("/getCompleteTopicByNameAndDomainName")
    @ApiOperation(value = "获得指定课程、指定主题的所有信息", notes = "获得指定课程、指定主题的所有信息")
    public ResponseEntity getCompleteTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName){
        Result result = topicService.findCompleteTopicByNameAndDomainName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 获得指定课程、指定主题的所有信息，添加hasFragment,如果为false分面树信息不添加碎片
     * 获得指定课程下指定主题的所有信息
     * */
    @PostMapping("/getCompleteTopicByNameAndDomainNameWithHasFragment")
    @ApiOperation(value = "获得指定课程、指定主题的所有信息", notes = "获得指定课程、指定主题的所有信息")
    public ResponseEntity getCompleteTopicByNameAndDomainNameWithHasFragment(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "hasFragment") boolean hasFragment){
        if(hasFragment == true){
            Result result = topicService.findCompleteTopicByNameAndDomainName(domainName, topicName);
            if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        Result result = topicService.findCompleteTopicByNameAndDomainNameWithoutAssemble(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }





}
