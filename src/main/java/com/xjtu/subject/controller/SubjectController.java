package com.xjtu.subject.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.service.SubjectGephiDemoAdjust;
import com.xjtu.subject.service.SubjectService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * api:处理subject学科数据
 *
 * @author:yangkuan
 * @date:2018/03/06 15:36
 */

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SubjectGephiDemoAdjust subjectGephiDemoAdjust; //导航布局增删功能

    @GetMapping(value = "/getSubjects")
    @ApiOperation(value = "获得所有学科信息", notes = "获得所有学科信息")
    public ResponseEntity getSubjects() {
        Result result = subjectService.findSubjects();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
//**************************************************************************************************************
    @GetMapping(value = "getSubjectGraphByName") //返回学科图谱
    @ApiOperation(value = "返回学科图谱", notes = "返回学科图谱")
    public ResponseEntity getSubjectGraphByName(@RequestParam("subjectName") String subjectName) {
        Result result = subjectService.getSubjectGraphByName(subjectName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "增加学科课程，然后返回图谱", notes = "增加学科课程，然后返回图谱")
    @GetMapping(value = "getSubjectGraphByName/addClass") //增加节点后，返回图谱
    public ResponseEntity addGetSubjectGraphByName(@RequestParam("subjectName") String subjectName,@RequestParam("addDomainName") String addDomainName) {
        Result result = subjectGephiDemoAdjust.addDomainGenerateSubjectGraph(subjectName,addDomainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "删除学科课程，然后返回图谱", notes = "删除学科课程，然后返回图谱")
    @GetMapping(value = "getSubjectGraphByName/removeClass") //删除节点后，返回图谱
    public ResponseEntity removeGetSubjectGraphByName(@RequestParam("subjectName") String subjectName,@RequestParam("removeDomainName") String removeDomainName) {
        Result result = subjectGephiDemoAdjust.removeDomainGenerateSubjectGraph(subjectName,removeDomainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
//***************************************************************************************************************

    @PostMapping(value = "/insertSubject")
    @ApiOperation(value = "插入一条学科信息", notes = "插入一条学科信息")
    public ResponseEntity insertSubject(@RequestParam("subjectName") String subjectName
            , @RequestParam("note") String note) {
        Subject subject = new Subject(subjectName, note);
        Result result = subjectService.insertSubject(subject);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 获得学科、课程和主题信息 getSubjectTree
     */
    @GetMapping(value = "/getSubjectTree")
    @ApiOperation(value = "获得所有学科、课程和主题信息", notes = "获得学科、课程和主题信息")
    public ResponseEntity getSubjectTree() {
        Result result = subjectService.findSubjectTree();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
