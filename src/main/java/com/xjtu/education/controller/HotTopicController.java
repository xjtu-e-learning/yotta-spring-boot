package com.xjtu.education.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.service.HotTopicService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 热度主题接口
 *
 * @author yangkuan
 */
@RestController
@RequestMapping("/hotTopic")
public class HotTopicController {

    @Autowired
    HotTopicService hotTopicService;

    /**
     * 保存热度主题
     *
     * @param domainId
     * @param hotTopics
     * @return
     */
    @GetMapping("/saveHotTopicsByDomainId")
    @ApiOperation(value = "保存热度主题", notes = "保存热度主题")
    public ResponseEntity saveHotTopicsByDomainId(@RequestParam("domainId") Long domainId
            , @RequestParam("hotTopics") String hotTopics) {
        Result result = hotTopicService.saveHotTopicsByDomainId(domainId, hotTopics);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 查询热度主题
     *
     * @param domainId
     * @return
     */
    @GetMapping("/getHotTopicsByDomainId")
    @ApiOperation(value = "查询热度主题", notes = "查询热度主题")
    public ResponseEntity getHotTopicsByDomainId(@RequestParam("domainId") Long domainId) {
        Result result = hotTopicService.findHotTopicsByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
