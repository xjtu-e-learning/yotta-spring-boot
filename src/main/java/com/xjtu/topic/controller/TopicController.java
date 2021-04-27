package com.xjtu.topic.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.service.DomainService;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.service.TopicService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * api:处理topic主题数据
 *
 * @author:yangkuan
 * @date:2018/03/09 16:40
 */
@RestController
@RequestMapping(value = "/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;
    @Autowired
    private DomainService domainService;


    /**
     * API
     * 添加一个主题
     * 在选定的课程下添加新主题
     */
    @GetMapping("/insertTopicByNameAndDomainName")
    @ApiOperation(value = "插入主题信息，指定插入课程名和主题名", notes = "插入主题信息，指定插入课程名和主题名")
    public ResponseEntity insertTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
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
     */
    @GetMapping("/deleteTopicByNameAndDomainName")
    @ApiOperation(value = "删除主题", notes = "根据课程名和主题名进行删除")
    public ResponseEntity deleteTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = topicService.deleteTopicByNameAndDomainName(topicName, domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据主题ID删除主题
     */
    @GetMapping("/deleteTopicByTopicId")
    @ApiOperation(value = "根据主题ID删除主题", notes = "根据给出的主题ID进行主题及其下相关内容删除")
    public ResponseEntity deleteTopicByTopicId(@RequestParam(name = "topicId") Long topicId) {
        Result result = topicService.deleteTopicByTopicId(topicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 更新主题名
     * 根据旧主题名进行更新
     */
    @GetMapping("/updateTopicByTopicName")
    @ApiOperation(value = "更新主题名", notes = "根据旧主题名进行更新")
    public ResponseEntity updateTopicByTopicName(@RequestParam(name = "oldTopicName") String oldTopicName
            , @RequestParam(name = "newTopicName") String newTopicName
            , @RequestParam(name = "domainName") String domainName) {
        Result result = topicService.updateTopicByName(oldTopicName, newTopicName, domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getTopicsByDomainName")
    @ApiOperation(value = "获得主题信息", notes = "输入课程名，获得课程下主题信息")
    public ResponseEntity getTopicsByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = topicService.findTopicsByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getSelectedTopicsByDomainName")
    @ApiOperation(value = "获得算法过滤抽取后的知识主题", notes = "输入课程名，获得课程下抽取得到的主题信息")
    public ResponseEntity getSelectedTopicsByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = topicService.findSelectedTopicsByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getSelectedTopicsByDomainNameOutputToFiles")
    @ApiOperation(value = "分别获得算法过滤抽取前后的知识主题", notes = "输入课程名，获得课程下算法过滤前后的主题")
    public ResponseEntity getSelectedTopicsByDomainNameOutputToFiles(@RequestParam(name = "domainName") String domainName) {

        Result result= topicService.findTopicNameBeforeAndAfterFilter(domainName);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getTopicsByDomainId")
    @ApiOperation(value = "获得主题信息", notes = "输入课程id，获得课程下主题信息")
    public ResponseEntity getTopicsByDomainId(@RequestParam(name = "domainId") Long domainId) {
        Result result = topicService.findTopicsByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getSelectedTopicsByDomainId")
    @ApiOperation(value = "获得算法过滤抽取后的知识主题", notes = "输入课程Id，获得课程下抽取得到的主题信息")
    public ResponseEntity getSelectedTopicsByDomainId(@RequestParam(name = "domainId") Long domainId) {
        Result result = topicService.findSelectedTopicsByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @GetMapping("/filterTopics")
    @ApiOperation(value = "利用算法对主题进行过滤，删除多余的主题以及主题下所有内容", notes = "输入课程名，删除算法过滤掉的主题以及主题下所有内容")
    public ResponseEntity filterTopics() {
        String[] notFilterDomain={"计算机组成原理","C语言","操作系统","计算机系统结构","数据结构",
        "数据库应用","低年级(1-2)语文","低年级(1-2)科学","中年级(3-4)英语","高年级(5-6)数学",
        "五年级科学","七年级语文","八年级语文","九年级语文","七年级数学","八年级数学","九年级数学",
        "七年级英语","八年级英语","九年级英语","八年级物理","九年级物理","九年级化学","七年级生物",
        "八年级生物","七年级历史","八年级历史","九年级历史","七年级地理","八年级地理","七年级政治","八年级政治",
        "九年级政治","初中信息技术","高一语文","高二语文","高中数学","高一英语","高三英语","高一历史","高二历史",
        "高一政治","高二政治","高三政治","高一地理","高三地理","高一生物","高二生物","高一化学","高二化学","高三化学",
        "合同法","数据结构(人工)","示范课程高等数学","概率论"};
        Map<String,Object > map=new HashMap<>();
        List<String> allDomainName=new ArrayList<>();
        List<Domain> domains = (List<Domain>) domainService.findDomains().getData();
        List<String> filterDomainNames= Arrays.asList(notFilterDomain);
        for (int i = 0; i < domains.size(); i++) {
            Domain domain = domains.get(i);
            String domainName = domain.getDomainName();
            allDomainName.add(domainName);
            if (!filterDomainNames.contains(domainName)) {
                Result result = topicService.filterTopicsByDomainName(domainName);
                if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                    map.put(domainName, result);
                    continue;
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
                }
            }
        }
        allDomainName.removeAll(filterDomainNames);

        map.put("已经过滤的主题",allDomainName);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }



    @GetMapping("/filterTopicsByDomainName")
    @ApiOperation(value = "利用算法对主题进行过滤，删除多余的主题以及主题下所有内容", notes = "输入课程名，删除算法过滤掉的主题以及主题下所有内容")
    public ResponseEntity filterTopicsByDomainName(@RequestParam("domainName")String domainName) {
        Result result = topicService.filterTopicsByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getTopicNumByDomainName")
    @ApiOperation(value = "通过课程名获取该课程下的主题数量", notes = "通过课程名获取该课程下的主题数量")
    public ResponseEntity getTopicNumByDomainName(@RequestParam("domainName")String domainName) {

        Result result = topicService.getTopicNumByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getTopicListByTopicNumLargerThanNum")
    @ApiOperation(value = "通过输入主题数量，得到主题数大于该值的所有课程", notes = "通过输入主题数量，得到主题数大于该值的所有课程")
    public ResponseEntity getTopicListByTopicNumLargerThanNum(@RequestParam("topicNum")Integer topicNum) {

        Result result = topicService.getTopicListByTopicNumLargerThanNum(topicNum);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/deleteNeedlessTopicByDomainName")
    @ApiOperation(value = "输入课程名称和指定主题数量，删除该课程下主题数量超过指定数量的部分", notes = "输入课程名称和指定主题数量，删除该课程下主题数量超过指定数量的部分")
    public ResponseEntity deleteNeedlessTopicByDomainName(@RequestParam("domainName")String domainName,
                                                          @RequestParam("num")Integer num) {

        Result result = topicService.deleteNeedlessTopicByDomainName(domainName,num);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }





    /**
     * API
     * 获得指定课程、指定主题的所有信息
     * 获得指定课程下指定主题的所有信息
     */
    @GetMapping("/getCompleteTopicByNameAndDomainName")
    @ApiOperation(value = "获得指定课程、指定主题的所有信息", notes = "获得指定课程、指定主题的所有信息")
    public ResponseEntity getCompleteTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = topicService.findCompleteTopicByNameAndDomainName(domainName, topicName, "true");
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 获得指定课程、指定主题的所有信息，添加hasFragment,如果为false分面树信息不添加碎片
     * 获得指定课程下指定主题的所有信息
     */
    @PostMapping("/getCompleteTopicByNameAndDomainNameWithHasFragment")
    @ApiOperation(value = "获得指定课程、指定主题的所有信息，用于构建分面树"
            , notes = "获得指定课程、指定主题的所有信息，用于构建分面树")
    public ResponseEntity getCompleteTopicByNameAndDomainNameWithHasFragment(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "hasFragment") String hasFragment) {
        if ("true".equals(hasFragment) || "emptyAssembleContent".equals(hasFragment)) {
            Result result = topicService.findCompleteTopicByNameAndDomainName(domainName, topicName, hasFragment);
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

    /**
     * API
     * 获得指定课程第一个主题的所有信息,用于构建分面树
     */
    @PostMapping("/getFirstTopicByDomainName")
    @ApiOperation(value = "获得指定课程第一个主题的所有信息,用于构建分面树", notes = "获得指定课程第一个主题的所有信息,用于构建分面树")
    public ResponseEntity getFirstTopicByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = topicService.findFirstTopicByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 查询指定课程、主题下的，主题信息、以及分面统计信息
     */
    @GetMapping("/getTopicInformationByDomainNameAndTopicName")
    @ApiOperation(value = "查询指定课程、主题下的，主题信息、以及分面统计信息"
            , notes = "查询指定课程、主题下的，主题信息、以及分面统计信息")
    public ResponseEntity getTopicInformationByDomainNameAndTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = topicService.findTopicInformationByDomainNameAndTopicName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/getCompleteTopicByTopicName")
    @ApiOperation(value = "根据主题名，获得给定主题的所有信息，用于构建分面树"
            , notes = "根据主题名，获得给定主题的所有信息，用于构建分面树")
    public ResponseEntity getCompleteTopicByTopicName(@RequestParam(name = "topicName") String topicName,
                                                      @RequestParam(name = "hasFragment") String hasFragment) {
        Result result = topicService.getCompleteTopicByTopicName(topicName, hasFragment);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }





    @PostMapping("/deleteCompleteTopicByDomainName")
    @ApiOperation(value = "根据课程名，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！", notes = "根据课程名，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！")
    public ResponseEntity deleteCompleteTopicByDoaminName(@RequestParam(name = "domainName") String domainName) {
        Result result = topicService.deleteCompleteTopicByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/deleteTopicCompleteByDomainNameAndTopicName")
    @ApiOperation(value = "根据课程名和主题名，递归地依次删除该课程对应主题下的主题依赖关系、碎片、分面、主题，请谨慎操作！", notes = "根据课程名和主题名，递归地依次删除该课程对应主题下的主题依赖关系、碎片、分面、主题，请谨慎操作！")
    public ResponseEntity deleteTopicCompleteByDomainNameAndTopicName(@RequestParam(name = "domainName") String domainName,
                                                              @RequestParam(name = "topicName") String topicName) {
        Result result = topicService.deleteTopicCompleteByDomainNameAndTopicName(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteTopicCompleteByDomainNameAndTopicId")
    @ApiOperation(value = "根据课程名和主题名，递归地依次删除该课程对应主题下的主题依赖关系、碎片、分面、主题，请谨慎操作！", notes = "根据课程名和主题名，递归地依次删除该课程对应主题下的主题依赖关系、碎片、分面、主题，请谨慎操作！")
    public ResponseEntity deleteTopicCompleteByDomainNameAndTopicId(@RequestParam(name = "domainName") String domainName,
                                                                      @RequestParam(name = "topicId") Long topicId) {
        Result result = topicService.deleteTopicCompleteByDomainNameAndTopicId(domainName,topicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @PostMapping("/deleteDuplicatedTopicCompleteByDomainName")
    @ApiOperation(value = "根据课程名删除该课程下存在的重复主题", notes = "根据课程名删除该课程下存在的重复主题")
    public ResponseEntity deleteDuplicatedTopicCompleteByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = topicService.deleteDuplicatedTopicCompleteByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @PostMapping("/deleteCompleteTopicByDomainId")
    @ApiOperation(value = "根据课程ID，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！", notes = "根据课程名，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！")
    public ResponseEntity deleteCompleteTopicByDoaminName(@RequestParam(name = "domainId") Long domainId) {
        Result result = topicService.deleteCompleteTopicByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 一键清除野生主题、分面、碎片及主题依赖关系，解决数据库冗余
     * @return
     * @author  Qi Jingchao
     */
    @GetMapping("/deleteNonDomainTopicAndFacetAndAssembleAndDependency")
    @ApiOperation(value = "一键清除野生主题、分面、碎片及主题依赖关系", notes = "一键清除野生主题、分面、碎片及主题依赖关系")
    public ResponseEntity deleteNonDomainTopicAndFacetAndAssembleAndDependency() {
        Result result = topicService.deleteNonDomainTopicAndFacetAndAssembleAndDependency();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/insertNewCompleteTopicByNameAndDomainName")
    @ApiOperation(value = "（谨慎使用，比较耗时！）插入新主题，调用爬虫获取主题分面，调用算法添加主题依赖关系", notes = "（谨慎使用，比较耗时！）插入新主题，调用爬虫获取主题分面，调用算法添加主题依赖关系")
    public ResponseEntity insertNewCompleteTopicByNameAndDomainName(@RequestParam(name = "domainName") String domainName,
                                                                    @RequestParam(name = "topicName") String topicName) {
        Result result = topicService.insertNewCompleteTopicByNameAndDomainName(topicName,domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
