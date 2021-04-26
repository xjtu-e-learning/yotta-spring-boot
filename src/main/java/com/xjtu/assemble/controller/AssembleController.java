package com.xjtu.assemble.controller;

import com.xjtu.assemble.service.AssembleService;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.facet.repository.FacetRepository;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
            , @RequestParam(name = "topicName") String topicName) {
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
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName) {
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
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName) {
        Result result = assembleService.findAssemblesInSecondLayerFacet(domainName, topicName
                , firstLayerFacetName, secondLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/getAssemblesByDomainNameAndTopicNamesAndUserId")
    @ApiOperation(value = "指定课程名、主题名列表，查询其下碎片"
            , notes = "指定课程名、主题名列表，查询其下碎片")
    public ResponseEntity getAssemblesByDomainNameAndTopicNamesAndUserId(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNames") String topicNames
            , @RequestParam(name = "userId") Long userId) {
        Result result = assembleService.findAssemblesByDomainNameAndTopicNamesAndUserId(domainName, topicNames, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/getAssemblesByTopicIdAndUserIdAndPagingAndSorting")
    @ApiOperation(value = "指定主题id、用户id、请求碎片类型、分页信息，查询其下碎片"
            , notes = "指定主题id、用户id、请求碎片类型、分页信息，查询其下碎片")
    public ResponseEntity getAssemblesByTopicIdAndUserIdAndPagingAndSorting(
            @RequestParam(name = "topicId") Long topicId
            , @RequestParam(name = "userId") Long userId
            , @RequestParam(name = "requestType", defaultValue = "text") String requestType
            , @RequestParam(name = "page") Integer page
            , @RequestParam(name = "size") Integer size
            , @RequestParam(name = "ascOrder", defaultValue = "false") boolean ascOrder) {
        Result result = assembleService.findAssemblesByTopicIdAndUserIdAndPagingAndSorting(topicId, userId
                , requestType, page, size, ascOrder);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/getAssemblesByFacetIdAndUserIdAndPagingAndSorting")
    @ApiOperation(value = "指定分面id、用户id、请求碎片类型、分页信息，查询其下碎片"
            , notes = "指定分面id、用户id、请求碎片类型、分页信息，查询其下碎片")
    public ResponseEntity getAssemblesByFacetIdAndUserIdAndPagingAndSorting(
            @RequestParam(name = "facetId") Long facetId
            , @RequestParam(name = "userId") Long userId
            , @RequestParam(name = "requestType", defaultValue = "text") String requestType
            , @RequestParam(name = "page") Integer page
            , @RequestParam(name = "size") Integer size
            , @RequestParam(name = "ascOrder", defaultValue = "false") boolean ascOrder) {
        Result result = assembleService.findAssemblesByFacetIdAndUserIdAndPagingAndSorting(facetId, userId
                , requestType, page, size, ascOrder);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/getAssemblesByDomainNameAndTopicNamesAndUserIdSplitByType")
    @ApiOperation(value = "指定课程名、主题名列表，查询其下两种类型的碎片"
            , notes = "指定课程名、主题名列表，查询其下两种类型的碎片")
    public ResponseEntity getAssemblesByDomainNameAndTopicNamesAndUserIdSplitByType(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNames") String topicNames
            , @RequestParam(name = "userId") Long userId) {
        Result result = assembleService.findAssemblesByDomainNameAndTopicNamesAndUserIdSplitByType(domainName, topicNames, userId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getAssemblesInThirdLayerFacet")
    @ApiOperation(value = "指定课程名、主题名和一、二、三级分面名，查询三级分面下的碎片"
            , notes = "指定课程名、主题名和三级分面名，查询三级分面下的碎片")
    public ResponseEntity getAssemblesInThirdLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName
            , @RequestParam(name = "thirdLayerFacetName") String thirdLayerFacetName) {
        Result result = assembleService.findAssemblesInThirdLayerFacet(domainName, topicName
                , firstLayerFacetName, secondLayerFacetName, thirdLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /*@PostMapping("/insertAssemble")
    @ApiOperation(value = "从暂存表中添加碎片到碎片表中，并删除暂存表中的碎片"
            , notes = "从暂存表中添加碎片到碎片表中，并删除暂存表中的碎片")
    public ResponseEntity insertAssemble(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "facetLayer") Integer facetLayer
            , @RequestParam(name = "temporaryAssembleId") Long temporaryAssembleId) {
        Result result = assembleService.insertAssemble(domainName
                , topicName, facetName
                , facetLayer, temporaryAssembleId
                , "人工");
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }*/

    @PostMapping("/appendAssemble")
    @ApiOperation(value = "添加碎片到碎片表中"
            , notes = "添加碎片到碎片表中")
    public ResponseEntity appendAssemble(
            @RequestParam(name = "sourceName", defaultValue = "人工") String sourceName
            , @RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "facetId") Long facetId
            , @RequestParam(name = "assembleContent") String assembleContent
            , @RequestParam(name = "url") String url) {
        Result result = assembleService.insertAssemble(facetId, assembleContent, sourceName, domainName, url);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/insertTemporaryAssemble")
    @ApiOperation(value = "添加碎片到碎片暂存表"
            , notes = "添加碎片到碎片暂存表")
    public ResponseEntity insertTemporaryAssemble(@RequestParam(name = "assembleContent") String assembleContent
            , @RequestParam(name = "userName") String userName) {
        Result result = assembleService.insertTemporaryAssemble(assembleContent, userName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/getTemporaryAssemblesByUserName")
    @ApiOperation(value = "根据用户名从暂存表中查询碎片"
            , notes = "根据用户名从暂存表中查询碎片")
    public ResponseEntity getTemporaryAssemblesByUserName(@RequestParam(name = "userName") String userName) {
        Result result = assembleService.findTemporaryAssemblesByUserName(userName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/getTemporaryAssembleById")
    @ApiOperation(value = "根据碎片id从暂存表中查询碎片"
            , notes = "根据碎片id从暂存表中查询碎片")
    public ResponseEntity getTemporaryAssembleById(@RequestParam(name = "assembleId") Long assembleId) {
        Result result = assembleService.findTemporaryAssembleById(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getAssembleById")
    @ApiOperation(value = "根据碎片id从碎片表中查询碎片"
            , notes = "根据碎片id从碎片表中查询碎片")
    public ResponseEntity getAssembleById(@RequestParam(name = "assembleId") Long assembleId) {
        Result result = assembleService.findAssembleById(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getAssemblesByFacetId")
    @ApiOperation(value = "根据分面id从碎片表中查询碎片"
            , notes = "根据分面id从碎片表中查询碎片")
    public ResponseEntity getAssemblesByFacetId(@RequestParam(name = "facetId") Long facetId) {
        Result result = assembleService.findAssemblesByFacetId(facetId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Autowired
    FacetRepository facetRepository;

    @GetMapping("/test")
    @ApiOperation(value = "根据分面id从碎片表中查询碎片"
            , notes = "根据分面id从碎片表中查询碎片")
    public ResponseEntity test(@RequestParam(name = "facetId") Long facetId) {
        List<Long> facetIds = new ArrayList<>();
        facetIds.add(facetId);
        return ResponseEntity.status(HttpStatus.OK).body(facetRepository.findFacetIdsByParentFacetIds(facetIds));
    }

    @GetMapping("/getAssembleContentById")
    @ApiOperation(value = "根据碎片id从碎片表中查询碎片内容"
            , notes = "根据碎片id从碎片表中查询碎片内容")
    public ResponseEntity getAssembleContentById(@RequestParam(name = "assembleId") Long assembleId) {
        String result = assembleService.findAssembleContentById(assembleId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/updateTemporaryAssemble")
    @ApiOperation(value = "根据碎片Id，更新暂存表中的碎片内容"
            , notes = "根据碎片Id，更新暂存表中的碎片内容")
    public ResponseEntity updateTemporaryAssemble(@RequestParam(name = "assembleId") Long assembleId
            , @RequestParam(name = "assembleContent") String assembleContent) {
        Result result = assembleService.updateTemporaryAssemble(assembleId, assembleContent);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/updateAssemble")
    @ApiOperation(value = "根据碎片Id，更新碎片表中的碎片内容、数据源以及链接"
            , notes = "根据碎片Id，更新碎片表中的碎片内容、数据源以及链接")
    public ResponseEntity updateAssemble(@RequestParam(name = "assembleId") Long assembleId
            , @RequestParam(name = "assembleContent") String assembleContent
            , @RequestParam(name = "sourceName") String sourceName
            , @RequestParam(name = "url") String url) {
        Result result = assembleService.updateAssemble(assembleId, assembleContent, sourceName, url);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/deleteTemporaryAssemble")
    @ApiOperation(value = "根据碎片Id，从碎片暂存表中删除碎片"
            , notes = "根据碎片Id，从碎片暂存表中删除碎片")
    public ResponseEntity deleteTemporaryAssemble(@RequestParam(name = "assembleId") Long assembleId) {
        Result result = assembleService.deleteTemporaryAssemble(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/deleteAssemble")
    @ApiOperation(value = "根据碎片Id，从碎片表中删除碎片"
            , notes = "根据碎片Id，从碎片表中删除碎片")
    public ResponseEntity deleteAssemble(@RequestParam(name = "assembleId") Long assembleId) {
        Result result = assembleService.deleteAssemble(assembleId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 根据课程ID，从碎片表中删除碎片
     *
     * @param domainId
     * @return
     * @author Qi Jingchao
     */
    @GetMapping("/deleteAssembleByDomainId")
    @ApiOperation(value = "根据课程ID，从碎片表中删除碎片", notes = "根据课程ID，从碎片表中删除碎片")
    public ResponseEntity deleteAssembleByDomainId(@RequestParam(name = "domainId") Long domainId) {
        Result result = assembleService.deleteAssembleByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 根据课程名，从碎片表中删除碎片
     *
     * @param domainName
     * @return
     * @author Qi Jingchao
     */
    @GetMapping("/deleteAssembleByDomainName")
    @ApiOperation(value = "根据课程名，从碎片表中删除碎片", notes = "根据课程名，从碎片表中删除碎片")
    public ResponseEntity deleteAssembleByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = assembleService.deleteAssembleByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/uploadImageWithId")
    @ApiOperation(value = "上传图片到服务器"
            , notes = "上传图片到服务器")
    public ResponseEntity uploadImageWithId(@RequestParam(value = "facetId") Long facetId,
                                            @RequestParam(value = "assembleId") Long assembleId,
                                            @RequestParam(value = "image") MultipartFile image) {
        Result result = assembleService.uploadImage(facetId, assembleId, image);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/uploadImage")
    @ApiOperation(value = "上传图片到服务器"
            , notes = "上传图片到服务器")
    public ResponseEntity uploadImage(@RequestParam(value = "image") MultipartFile image) {
        Map<String, Object> result = assembleService.uploadImage(image);
        if ((Integer) result.get("errno") != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/countUpdateAssemble")
    @ApiOperation(value = "根据课程名统计近一个月更新碎片数量", notes = "根据课程名统计近一个月更新碎片数量")
    public ResponseEntity countUpdateAssembleByDomainName(@RequestParam(value = "domainName") String domainName) {
        Result result = assembleService.countUpdateAssemble(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 修复域名迁移造成的图片失效问题，根据碎片ID和内容更新碎片assembleContent字段内容，注意：这不会改变assembleText。
     *
     * @param domainName
     * @return
     * @author Qi Jingchao
     */
    @PostMapping("/fixAssembleImageByDomainName")
    @ApiOperation(value = "修复域名迁移造成的图片失效问题", notes = "修复域名迁移造成的图片失效问题")
    public ResponseEntity fixAssembleImageByDomainName(@RequestParam(value = "domainName") String domainName) {
        Result result = assembleService.fixAssembleImageByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 供标准爬虫使用（使用标准一级分面的爬虫），判断碎片装配的分面是否正确
     *
     * @param facetName
     * @param assembleContent
     * @return
     * @author Qi Jingchao
     */
    @PostMapping("/isAssembleFacetMatchByAssembleAndFacet")
    @ApiOperation(value = "判断碎片装配的分面是否正确", notes = "限标准一级分面，包括：定义、性质、历史、应用、原理等")
    public ResponseEntity isAssembleFacetMatchByAssembleAndFacet(@RequestParam(value = "facetName") String facetName,
                                                                 @RequestParam(value = "assembleContent") String assembleContent) {
        Result result = assembleService.isAssembleFacetMatchByAssembleAndFacet(facetName, assembleContent);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 为碎片匹配分面（不入库，仅供测试），分配的分面限标准一级分面，包括：定义、性质、历史、应用、原理、类型、利弊等
     *
     * @param assembleContent
     * @return
     * @author Qi Jingchao
     */
    @PostMapping("/assignFacetForAssembleByAssembleContent")
    @ApiOperation(value = "为碎片匹配分面（不入库，仅供测试）", notes = "分配的分面限标准一级分面，包括：定义、性质、历史、应用、原理、类型、利弊等")
    public ResponseEntity assignFacetForAssembleByAssembleContent(@RequestParam(value = "assembleContent") String assembleContent) {
        Result result = assembleService.assignFacetForFacet(assembleContent);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * 判断分面碎片是否匹配，匹配则返回true，不匹配则为碎片匹配分面并返回分面名，分配的分面限标准一级分面，包括：定义、性质、历史、应用、原理、类型、利弊等
     * @param facetName
     * @param assembleContent
     * @return
     * @author Qi Jingchao
     */
    @PostMapping("/judgeAndAssignFacetForAssembleByAssembleContent")
    @ApiOperation(value = "为碎片匹配分面（不入库，仅供测试）", notes = "分配的分面限标准一级分面，包括：定义、性质、历史、应用、原理、类型、利弊等")
    public ResponseEntity judgeAndAssignFacetForAssembleByFacetNameAndAssembleContent(@RequestParam(value = "facetName") String facetName,
                                                                                      @RequestParam(value = "assembleContent") String assembleContent) {
        Result matchResult = assembleService.isAssembleFacetMatch(facetName, assembleContent);
        boolean isRight = (boolean) matchResult.getData();

        if (isRight) {
            if (!matchResult.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(matchResult);
            }
            return ResponseEntity.status(HttpStatus.OK).body(matchResult);
        }else{
            Result result = assembleService.assignFacetForFacet(assembleContent);
            if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }


    /**
     * 超长碎片分割（不入库，仅供测试），仅供博客、百科等文章式长碎片。
     *
     * @param assembleContent
     * @return
     * @author Qi Jingchao
     */
    @PostMapping("/assembleSegment")
    @ApiOperation(value = "超长碎片分割（不入库，仅供测试）", notes = "仅供博客、百科等文章式长碎片")
    public ResponseEntity assembleSegment(@RequestParam(value = "assembleContent") String assembleContent) {
        Result result = assembleService.assembleSegment(assembleContent);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
