package com.xjtu.assemble.controller;

import com.xjtu.assemble.service.AssembleService;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.utils.HttpUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * api: 处理assemble碎片
 *
 * @author yangkuan
 * @date 2018/03/15 14:43
 */
@RestController
@RequestMapping("/assemble")
public class AssembleController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AssembleService assembleService;


    @GetMapping("/getAssemblesInTopic")
    @ApiOperation(value = "指定课程名、主题名，查询该主题下的碎片", notes = "指定课程名、主题名和一级分面名，查询该主题下的碎片")
    public ResponseEntity getAssemblesInTopic(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssemblesInTopic(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getAssemblesInFirstLayerFacet")
    @ApiOperation(value = "指定课程名、主题名和一级分面名，查询一级分面下的碎片"
            , notes = "指定课程名、主题名和一级分面名，查询一级分面下的碎片")
    public ResponseEntity getAssemblesInFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssemblesInFirstLayerFacet(domainName, topicName, firstLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getAssemblesInSecondLayerFacet")
    @ApiOperation(value = "指定课程名、主题名和二级分面名，查询二级分面下的碎片"
            , notes = "指定课程名、主题名和二级分面名，查询二级分面下的碎片")
    public ResponseEntity getAssemblesInSecondLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssemblesInSecondLayerFacet(domainName, topicName, secondLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getAssemblesByDomainNameAndTopicNames")
    @ApiOperation(value = "指定课程名、主题名列表，查询其下碎片"
            , notes = "指定课程名、主题名列表，查询其下碎片")
    public ResponseEntity getAssemblesByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNames") String topicNames
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssemblesByDomainNameAndTopicNames(domainName, topicNames);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getAssemblesInThirdLayerFacet")
    @ApiOperation(value = "指定课程名、主题名和三级分面名，查询三级分面下的碎片"
            , notes = "指定课程名、主题名和三级分面名，查询三级分面下的碎片")
    public ResponseEntity getAssemblesInThirdLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "thirdLayerFacetName") String thirdLayerFacetName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssemblesInThirdLayerFacet(domainName, topicName, thirdLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/insertAssemble")
    @ApiOperation(value = "从暂存表中添加碎片到碎片表中，并删除暂存表中的碎片"
            , notes = "从暂存表中添加碎片到碎片表中，并删除暂存表中的碎片")
    public ResponseEntity insertAssemble(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "facetLayer") Integer facetLayer
            , @RequestParam(name = "temporaryAssembleId") Long temporaryAssembleId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.insertAssemble(domainName
                , topicName, facetName
                , facetLayer, temporaryAssembleId
                , "人工");
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/insertTemporaryAssemble")
    @ApiOperation(value = "添加碎片到碎片暂存表"
            , notes = "添加碎片到碎片暂存表")
    public ResponseEntity insertTemporaryAssemble(@RequestParam(name = "assembleContent") String assembleContent
            , @RequestParam(name = "userName") String userName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.insertTemporaryAssemble(assembleContent, userName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/getTemporaryAssemblesByUserName")
    @ApiOperation(value = "根据用户名从暂存表中查询碎片"
            , notes = "根据用户名从暂存表中查询碎片")
    public ResponseEntity getTemporaryAssemblesByUserName(@RequestParam(name = "userName") String userName
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findTemporaryAssemblesByUserName(userName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getTemporaryAssembleById")
    @ApiOperation(value = "根据碎片id从暂存表中查询碎片"
            , notes = "根据碎片id从暂存表中查询碎片")
    public ResponseEntity getTemporaryAssembleById(@RequestParam(name = "assembleId") Long assembleId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findTemporaryAssembleById(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getAssembleById")
    @ApiOperation(value = "根据碎片id从碎片表中查询碎片"
            , notes = "根据碎片id从碎片表中查询碎片")
    public ResponseEntity getAssembleById(@RequestParam(name = "assembleId") Long assembleId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.findAssembleById(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/updateTemporaryAssemble")
    @ApiOperation(value = "根据碎片Id，更新暂存表中的碎片内容"
            , notes = "根据碎片Id，更新暂存表中的碎片内容")
    public ResponseEntity updateTemporaryAssemble(@RequestParam(name = "assembleId") Long assembleId
            , @RequestParam(name = "assembleContent") String assembleContent
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.updateTemporaryAssemble(assembleId, assembleContent);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @GetMapping("/deleteTemporaryAssemble")
    @ApiOperation(value = "根据碎片Id，从碎片暂存表中删除碎片"
            , notes = "根据碎片Id，从碎片暂存表中删除碎片")
    public ResponseEntity deleteTemporaryAssemble(@RequestParam(name = "assembleId") Long assembleId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.deleteTemporaryAssemble(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/deleteAssemble")
    @ApiOperation(value = "根据碎片Id，从碎片表中删除碎片"
            , notes = "根据碎片Id，从碎片表中删除碎片")
    public ResponseEntity deleteAssemble(@RequestParam(name = "assembleId") Long assembleId
            , HttpServletRequest request) {
        logger.info(HttpUtil.getHeaders(request));
        Result result = assembleService.deleteAssemble(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
