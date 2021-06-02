package com.xjtu.dependency.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.service.DependencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * api:处理主题依赖关系
 *
 * @author:yangkuan
 * @date:2018/03/21 13:19
 */
@RestController
@RequestMapping(value = "dependency")
public class DependencyController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DependencyService dependencyService;


    @PostMapping("/insertDependency")
    @ApiOperation(value = "通过主课程名，在课程下的插入、添加主题依赖关系", notes = "通过主课程名，在课程下的插入、添加主题依赖关系")
    public ResponseEntity insertDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicName") String startTopicName
            , @RequestParam(name = "endTopicName") String endTopicName) {
        domainName=domainName.replaceAll("jiahao","+");
        startTopicName=startTopicName.replaceAll("jiahao","+");
        endTopicName=endTopicName.replaceAll("jiahao","+");
        Result result = dependencyService.insertDependency(domainName, startTopicName, endTopicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependency")
    @ApiOperation(value = "通过主课程名，起始、终止主题id删除依赖关系", notes = "通过主课程名，起始、终止主题id删除依赖关系")
    public ResponseEntity deleteDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicId") Long startTopicId
            , @RequestParam(name = "endTopicId") Long endTopicId) {
        Result result = dependencyService.deleteDependency(domainName, startTopicId, endTopicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependencyByTopicName")
    @ApiOperation(value = "通过主课程名，起始、终止主题名删除依赖关系", notes = "通过主课程名，起始、终止主题名删除依赖关系")
    public ResponseEntity deleteDependencyByTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicName") String startTopicName
            , @RequestParam(name = "endTopicName") String endTopicName) {
        domainName=domainName.replaceAll("jiahao","+");
        startTopicName=startTopicName.replaceAll("jiahao","+");
        endTopicName=endTopicName.replaceAll("jiahao","+");
        Result result = dependencyService.deleteDependencyByTopicName(domainName, startTopicName, endTopicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     */
    @GetMapping("/getDependenciesByKeyword")
    @ApiOperation(value = "通过关键词，获取该课程下的主题依赖关系", notes = "通过关键词，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByKeyword(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "keyword") String keyword) {
        Result result = dependencyService.findDependenciesByKeyword(domainName, keyword);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过课程名，获取该课程下的主题依赖关系
     */
    @GetMapping("/getDependenciesByDomainName")
    @ApiOperation(value = "通过课程名，获取该课程下的主题依赖关系", notes = "通过课程名，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = dependencyService.findDependenciesByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件
     */
    @PostMapping("/getDependenciesByDomainNameSaveAsGexf")
    @ApiOperation(value = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件"
            , notes = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件")
    public ResponseEntity getDependenciesByDomainNameSaveAsGexf(@RequestParam(name = "domainName") String domainName) {
        Result result = dependencyService.findDependenciesByDomainNameSaveAsGexf(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     */
    @PostMapping("/generateDependencyByDomainName")
    @ApiOperation(value = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息",
            notes = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息")
    public ResponseEntity generateDependencyByDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "isEnglish") boolean isEnglish) {
        Result result = dependencyService.generateDependencyByDomainName(domainName, isEnglish);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     */
    @PostMapping("/generateDependencyByDomainNameWithNewTopicName")
    @ApiOperation(value = "自动构建新生成的主题与课程原有的其他主题的依赖关系，需要指定课程名，自动添加与出度最高主题的依赖",
            notes = "自动构建新生成的主题与课程原有的其他主题的依赖关系，需要指定课程名，自动添加与出度最高主题的依赖")
    public ResponseEntity generateDependencyByDomainNameWithNewTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = dependencyService.generateDependencyByDomainNameWithNewTopicName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     */
    @PostMapping("/generateDependencyByDomainId")
    @ApiOperation(value = "自动构建主题依赖关系。通过课程id，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息",
            notes = "自动构建主题依赖关系。通过课程id，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息")
    public ResponseEntity generateDependencyByDomainId(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "isEnglish") boolean isEnglish) {
        Result result = dependencyService.generateDependencyByDomainId(domainId, isEnglish);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     */
    @PostMapping("/generateAllDomainDependency")
    @ApiOperation(value = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息",
            notes = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息")
    public ResponseEntity generateAllDomainDependency(@RequestParam(name="startIndex") Integer startIndex,
                                                      @RequestParam(name="endIndex") Integer endIndex) {
        Result result = dependencyService.generateAllDomainDependency(startIndex,endIndex);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @GetMapping("/LearningPathWeb")
    @ApiOperation(value = "智慧教育系统获得推荐路径", notes = "智慧教育系统获得推荐路径")
    public ResponseEntity getLearningPath(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "userId") Long userId) {
        Result result = dependencyService.getLearningPath(domainId, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/LearningPathWeb/updateUserStates")
    @ApiOperation(value = "智慧教育系统学习路径，更新用户状态", notes = "智慧教育系统学习路径，更新用户状态")
    public ResponseEntity learningPath_updateUserStates(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "userId") Long userId) {
        Result result = dependencyService.learningPath_updateUserStates(domainId, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/LearningPath/defineLearningPath")
    @ApiOperation(value = "智慧教育系统学习路径，定义学习路径", notes = "智慧教育系统学习路径，定义学习路径")
    public ResponseEntity learningPath_defineLearningPath(@RequestParam(name = "domainId") Long domainId
            , @RequestParam(name = "userId") Long userId
            , @RequestParam(name = "termId") Long termId) {
        Result result = dependencyService.learningPath_defineLearningPath(domainId, userId, termId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependenciesByDomainName")
    @ApiOperation(value = "通过课程名，删除课程下的所有主题依赖关系", notes = "通过课程名，删除课程下的所有主题依赖关系")
    public ResponseEntity deleteDependenciesByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = dependencyService.deleteDependenciesByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     * 该接口以前用来分析算法生成的依赖关系数据质量。后来不再使用。可以删除掉
     */
    @PostMapping("/getGenerateDependencyCSVFileByDomainName")
    @ApiOperation(value = "获得自动构建主题依赖关系CSV文件。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息",
            notes = "获得自动构建主题依赖关系CSV文件。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息")
    public ResponseEntity getGenerateDependencyCSVFileByDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "isEnglish") boolean isEnglish)
    {
        Result result = dependencyService.getGenerateDependencyCSVFileByDomainName(domainName, isEnglish);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 新增知识主题时，新增该单个知识主题与其它知识主题间的依赖关系
     */
    @PostMapping("/getGenerateDependencyWithNewTopic")
    @ApiOperation(value = "新增知识主题时，新增该知识主题与其它知识主题间的依赖关系",
            notes = "新增知识主题时，新增该知识主题与其它知识主题间的依赖关系")
    public ResponseEntity getGenerateDependencyWithNewTopic(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName)
    {
        Result result = dependencyService.generateDependencyWithNewTopic(domainName, topicName);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API，训练构建认知关系分类模型，使用数据库中数据结构等课程作为已标注数据进行训练
     */
    @PostMapping("/trainSVMModel")
    @ApiOperation(value = "训练构建认知关系分类模型",
            notes = "训练构建认知关系分类模型")
    public ResponseEntity trainSVMModel()
    {
        boolean isEnglish = false;
        Result result = dependencyService.trainSVMModel(isEnglish);
        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

//    /**
//     * API，训练构建认知关系分类模型
//     */
//    @PostMapping("/predictSVMModel")
//    @ApiOperation(value = "使用认知关系分类模型预测认知关系",
//            notes = "使用认知关系分类模型预测认知关系")
//    public ResponseEntity predictSVMModel(@RequestParam(name = "domainName") String domainName
//            , @RequestParam(name = "isEnglish") boolean isEnglish)
//    {
//        Result result = dependencyService.predictSVMModel(domainName,isEnglish);
//        if(!result.getCode().equals(ResultEnum.SUCCESS.getCode()))
//        {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }



}
