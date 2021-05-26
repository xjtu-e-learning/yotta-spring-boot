package com.xjtu.django.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class PythonService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Result killAndRestartPythonService(String port) {
        Runtime runtime=Runtime.getRuntime();
        if(getPID(port)!=null && getPID(port).length()==0){
            logger.info("开始杀死 django {} 服务的进程",port);
            killTask(getProgramName(getPID(port)));
        }
        try {
            String [] cmd={"cmd","/C","E:\\anaconda3\\anaconda3\\envs\\django\\python.exe G:\\python服务\\mysite-no-cache\\manage.py runserver 0.0.0.0:"+port};
//            String [] cmd={"cmd","/C","E:\\Software\\anconada\\envs\\django\\python.exe E:\\mysite-with-cache-test\\manage.py runserver 0.0.0.0:"+port};
            Process exec = runtime.exec(cmd);
            Thread.sleep(2000);
            logger.info("重新启动 django {} 服务的进程完成",port);
        } catch (Exception e) {
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

    public Result getDependencesByDomainName(String domainName,String port) {
        if("8083".equals(port) || "8087".equals(port)){
            return ResultUtil.error(ResultEnum.ARGUMENTS_DEVELOP_ERROR.getCode(), ResultEnum.ARGUMENTS_DEVELOP_ERROR.getMsg(), "不合法的端口号，请输入正确的django服务端口号");
        }
        RestTemplate restTemplate = new RestTemplate();
//        String url="http://47.95.145.72:"+port+"/dependences/?domainName="+domainName;
        String url="http://localhost:"+port+"/dependences/?domainName="+domainName;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("domainName",domainName);
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params,requestHeaders);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        }catch(Exception e){
            killAndRestartPythonService(port);
            ResponseEntity<String> result2 = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            if(result2.getStatusCode().equals(HttpStatus.OK)){
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result2.getBody());
            }
            return ResultUtil.error(result2.getStatusCodeValue(), result2.getStatusCode().getReasonPhrase());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result.getBody());
    }

}
