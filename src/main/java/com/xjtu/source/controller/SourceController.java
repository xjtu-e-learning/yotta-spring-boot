package com.xjtu.source.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.source.domain.Source;
import com.xjtu.source.service.SourceService;
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
 * api: 处理source数据源
 *
 * @author yuanhao
 * @date 2018/3/4 19:44
 */
@RestController
@RequestMapping("/source")
public class SourceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceService sourceService;

    @GetMapping(value = "/getSources")
    @ApiOperation(value = "查询所有数据源", notes = "查询所有数据源")
    public ResponseEntity getSources() {
        Result result = sourceService.getSource();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getSourceById")
    @ApiOperation(value = "根据数据源Id查询数据源", notes = "输入数据源Id，查询数据源信息")
    public ResponseEntity getSourceById(@RequestParam(value = "sourceId", defaultValue = "1") Long sourceId) {
        Result result = sourceService.getSourceById(sourceId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertSource")
//    @RequestMapping(value = "/dasd", method = RequestMethod.GET)
    @ApiOperation(value = "插入数据源", notes = "插入数据源")
    public ResponseEntity insertSource(Source source) {
        Result result = sourceService.insertSource(source);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/deleteSource")
    @ApiOperation(value = "删除数据源", notes = "删除数据源")
    public ResponseEntity deleteSource(@RequestParam(value = "sourceId", defaultValue = "1") Long sourceId) {
        Result result = sourceService.deleteSource(sourceId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/updateSource")
    @ApiOperation(value = "更新数据源", notes = "更新数据源")
    public ResponseEntity updateSource(@RequestParam(value = "sourceId", defaultValue = "1") Long sourceId
            , Source newSource) {
        Result result = sourceService.updateSource(sourceId, newSource);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getSourceByPagingAndSorting")
    @ApiOperation(value = "分页查询数据源", notes = "分页查询数据源")
    public ResponseEntity getSourceByPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder) {
        Result result = sourceService.getSourceByPagingAndSorting(page - 1, size, ascOrder);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getSourceByIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询数据源", notes = "根据查询条件，分页查询数据源")
    public ResponseEntity getSourceByIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "sourceId", defaultValue = "1") Long sourceId) {
        Result result = sourceService.getSourceByIdAndPagingAndSorting(page - 1, size, ascOrder, sourceId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
