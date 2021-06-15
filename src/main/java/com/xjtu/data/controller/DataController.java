package com.xjtu.data.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.data.service.DataService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;


@RestController
@RequestMapping(value = "/data")
public class DataController {
    @Autowired
    private DataService dataService;

    @GetMapping(value = "/assembleTrans")
    @ApiOperation(value = "将complete数据库中的中小学碎片更新到test数据库",notes = "将complete数据库中的中小学碎片更新到test数据库")
    public ResponseEntity assembleTrans(@RequestParam(name = "subjectId") Long subjectId) throws Exception {
        Result result = dataService.assembleTransfer(subjectId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/file/download")
    @ApiOperation(value = "EXCEL格式的中小学课程知识森林下载",notes = "EXCEL格式的中小学课程知识森林下载")
    public ResponseEntity<FileSystemResource> getFile() throws FileNotFoundException {
        String fileName="data.rar";
        String path = "E:/yotta_data";
        File file = new File(path + fileName);
        if (file.exists()) {
            return export(file);
        }
        System.out.println(file);
        return null;
    }

    public ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }



}
