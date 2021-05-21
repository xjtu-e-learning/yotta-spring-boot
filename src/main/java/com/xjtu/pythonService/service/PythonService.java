package com.xjtu.pythonService.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class PythonService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Result killPythonService() {
        Runtime runtime=Runtime.getRuntime();
        logger.info("开始杀死python8081服务的进程");
        killTask(getProgramName(getPID("8080")));
        try {
            runtime.exec("e:");
            runtime.exec("cd E:\\mysite-with-cache-test");
            runtime.exec("conda activate django");
            runtime.exec("python manage.py runserver 0.0.0.0:8081");
            logger.info("重新启动python8081服务的进程完成");
            runtime.exec("e:");
            runtime.exec("cd E:\\mysite-no-cache-test");
            runtime.exec("conda activate django");
            runtime.exec("python manage.py runserver 0.0.0.0:8082");
            logger.info("重新启动python8082服务的进程完成");
        } catch (IOException e) {
            logger.error("重新启动进程失败");
            e.printStackTrace();
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),null);
    }
    public static String getPID(String port){
        InputStream is = null;
        BufferedReader br = null;
        String pid = null;
        try {
            String[] args = new String[]{"cmd.exe","/c","netstat -aon|findstr",port};
            is = Runtime.getRuntime().exec(args).getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String temp = br.readLine();
            if(temp != null){
                String[] strs = temp.split("\\s");
                pid=strs[strs.length-1];
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pid;
    }

    //根据进程ID得到映像名称
    public static String getProgramName(String pid){
        InputStream is = null;
        BufferedReader br = null;
        String programName = null;
        try {
            String[] args = new String[]{"cmd.exe","/c","tasklist|findstr",pid};
            is = Runtime.getRuntime().exec(args).getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String temp = br.readLine();
            if(temp != null){
                String[] strs = temp.split("\\s");
                programName=strs[0];
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return programName;
    }
    //根据映像名称关闭进程
    public static void killTask(String programName){
        String[] args = new String[]{"Taskkill","/f","/IM",programName};
        try
        {
            Runtime.getRuntime().exec(args);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



}
