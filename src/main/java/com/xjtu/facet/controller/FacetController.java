package com.xjtu.facet.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.facet.service.FacetService;
import com.xjtu.utils.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * api: 处理facet分面
 *
 * @author yangkuan
 * @date 2018/3/12 15:34
 */
@RestController
@RequestMapping("/facet")
public class FacetController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FacetService facetService;

    @ApiOperation(value = "在指定的课程和主题下添加一级分面", notes = "在指定的课程和主题下添加一级分面")
    @GetMapping("/insertFirstLayerFacet")
    public ResponseEntity insertFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetName") String facetName) {
        Result result = facetService.insertFacetByDomainAndTopic(domainName, topicName
                , facetName, 1, new Long(0));
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "在指定的课程、主题和一级分面下添加二级分面", notes = "在指定的课程、主题和一级分面下添加二级分面")
    @GetMapping("/insertSecondLayerFacet")
    public ResponseEntity insertSecondLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName) {
        Result result = facetService.insertSecondLayerFacet(domainName, topicName
                , firstLayerFacetName, secondLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "在指定的课程、主题和一级分面、二级分面下添加三级分面"
            , notes = "在指定的课程、主题和一级分面、二级分面下添加三级分面")
    @GetMapping("/insertThirdLayerFacet")
    public ResponseEntity insertThirdLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName
            , @RequestParam(name = "thirdLayerFacetName") String thirdLayerFacetName) {
        Result result = facetService.insertThirdLayerFacet(domainName, topicName
                , firstLayerFacetName, secondLayerFacetName, thirdLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "指定课程、主题和一级分面，删除一级分面"
            , notes = "指定课程、主题和一级分面，删除一级分面")
    @GetMapping("/deleteFirstLayerFacet")
    public ResponseEntity deleteFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName) {
        Result result = facetService.deleteFirstLayerFacet(domainName, topicName, firstLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "指定课程、主题、分面层和分面id，删除分面"
            , notes = "指定课程、主题、分面层和分面id，删除分面")
    @GetMapping("/deleteFacet")
    public ResponseEntity deleteFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetLayer") Integer facetLayer
            , @RequestParam(name = "facetId") Long facetId) {
        Result result = facetService.deleteFacet(domainName, topicName, facetLayer, facetId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "指定课程、主题和二级分面，删除二级分面"
            , notes = "指定课程、主题和二级分面，删除二级分面")
    @GetMapping("/deleteSecondLayerFacet")
    public ResponseEntity deleteSecondLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName) {
        Result result = facetService.deleteSecondLayerFacet(domainName, topicName, secondLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "指定课程、主题和三级分面，删除三级分面"
            , notes = "指定课程、主题和三级分面，删除三级分面")
    @GetMapping("/deleteThirdLayerFacet")
    public ResponseEntity deleteThirdLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "thirdLayerFacetName") String thirdLayerFacetName) {
        Result result = facetService.deleteThirdLayerFacet(domainName, topicName, thirdLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @ApiOperation(value = "更新一级分面名"
            , notes = "更新一级分面名")
    @GetMapping("/updateFirstLayerFacet")
    public ResponseEntity updateFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "newFacetName") String newFacetName) {
        Result result = facetService.updateFirstLayerFacet(domainName, topicName
                , facetName, newFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "更新二级分面名"
            , notes = "更新二级分面名")
    @GetMapping("/updateSecondLayerFacet")
    public ResponseEntity updateSecondLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "newFacetName") String newFacetName) {
        Result result = facetService.updateSecondLayerFacet(domainName,
                topicName,
                firstLayerFacetName,
                facetName, newFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "更新三级分面名"
            , notes = "更新三级分面名")
    @GetMapping("/updateThirdLayerFacet")
    public ResponseEntity updateThirdLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "newFacetName") String newFacetName) {
        Result result = ResultUtil.error(ResultEnum.NOT_DEVELOP_ERROR.getCode(),
                ResultEnum.NOT_DEVELOP_ERROR.getMsg());
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "更新分面名"
            , notes = "更新分面名")
    @GetMapping("/updateFacet")
    public ResponseEntity updateFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetLayer") Integer facetLayer
            , @RequestParam(name = "facetId") Long facetId
            , @RequestParam(name = "newFacetName") String newFacetName) {
        Result result = facetService.updateFacet(domainName, topicName, facetLayer, facetId, newFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获得主题的所有分面信息", notes = "输入课程名和主题名，获得知识主题的所有分面信息，知识森林VR使用（吴科炜）")
    @GetMapping("/getFacetsInTopic")
    public ResponseEntity getFacetsInTopic(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = facetService.findFacetsInTopic(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据课程名查询分面", notes = "根据课程名查询分面")
    @GetMapping("/getByDomainName")
    public ResponseEntity getByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = facetService.findByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获得一级分面下的所有分面信息", notes = "输入课程名和主题名、一级分面名，获得该一级分面的所有分面信息")
    @GetMapping("/getFacetsInFirstLayerFacet")
    public ResponseEntity getFacetsInFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName) {
        Result result = facetService.findFacetsInFirstLayerFacet(domainName, topicName, firstLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获得指定课程、主题和一级分面下的二级分面数", notes = "获得指定课程、主题和一级分面下的二级分面数")
    @GetMapping("/getSecondLayerFacetNumber")
    public ResponseEntity getSecondLayerFacetNumber(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "firstLayerFacetName") String firstLayerFacetName) {
        Result result = facetService.findSecondLayerFacetNumber(domainName, topicName, firstLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获得指定课程、主题和二级分面下的三级分面数", notes = "获得指定课程、主题和二级分面下的三级分面数")
    @GetMapping("/getThirdLayerFacetNumber")
    public ResponseEntity getThirdLayerFacetNumber(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "secondLayerFacetName") String secondLayerFacetName) {
        Result result = facetService.findThirdLayerFacetNumber(domainName, topicName, secondLayerFacetName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @ApiOperation(value = "根据课程名及主题，查询推荐主题列表下所有分面(网院示范应用)", notes = "根据课程名及主题，查询推荐主题列表下所有分面(网院示范应用)")
    @GetMapping("/getFacetsByDomainNameAndTopicNames")
    public ResponseEntity getFacetsByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNames") String topicNames) {
        Result result = facetService.findFacetsByDomainNameAndTopicNames(domainName, topicNames);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @ApiOperation(value = "指定课程、主题，获得所有一级分面下的二级分面信息", notes = "指定课程、主题，获得所有一级分面下的二级分面信息")
    @GetMapping("/getSecondLayerFacetGroupByFirstLayerFacet")
    public ResponseEntity getSecondLayerFacetGroupByFirstLayerFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = facetService.findSecondLayerFacetGroupByFirstLayerFacet(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获取对应分面下的碎片数量", notes = "获取对应分面下的碎片数量")
    @GetMapping("/getAssembleNumberInFacet")
    public ResponseEntity getAssembleNumberInFacet(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetName") String facetName
            , @RequestParam(name = "facetLayer") Integer facetLayer) {
        Result result = facetService.findAssembleNumberInFacet(domainName, topicName, facetName, facetLayer);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "统计分面信息", notes = "统计分面信息")
    @GetMapping("/countFacetInfo")
    public ResponseEntity countFacetInfo(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName
            , @RequestParam(name = "facetLayer") Integer facetLayer
            , @RequestParam(name = "facetId") Long facetId) {
        Result result = facetService.countFacetInfo(domainName, topicName, facetLayer, facetId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "获取一门课程下的分面分布", notes = "获取一门课程下的分面分布")
    @GetMapping("/getFacetDistribution")
    public ResponseEntity getFacetDistribution(@RequestParam(name = "domainName") String domainName) {
        Result result = facetService.findFacetDistribution(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据课程名和主题名，查询一级分面", notes = "根据课程名和主题名，查询一级分面")
    @GetMapping("/getFirstLayerFacetsByDomainNameAndTopicName")
    public ResponseEntity getFirstLayerFacetsByDomainNameAndTopicName
            (@RequestParam(name = "domainName") String domainName,
             @RequestParam(name = "topicName") String topicName) {
        Result result = facetService.findFirstLayerFacetsByDomainNameAndTopicName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据主题id，查询一级分面", notes = "根据主题id，查询一级分面")
    @GetMapping("/getFirstLayerFacetsByTopicId")
    public ResponseEntity getFirstLayerFacetsByTopicId(@RequestParam(name = "topicId") Long topicId) {
        Result result = facetService.findFirstLayerFacetsByTopicId(topicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "根据分面id，查询分面名及父分面id、父分面名", notes = "根据分面id，查询分面名及父分面id、父分面名")
    @GetMapping("/getFacetNameAndParentFacetNameByFacetId")
    public ResponseEntity getFacetNameAndParentFacetNameByFacetId(@RequestParam(name = "facetId") Long facetId)
    {
        Result result = facetService.getFacetNameAndParentFacetNameByFacetId(facetId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
