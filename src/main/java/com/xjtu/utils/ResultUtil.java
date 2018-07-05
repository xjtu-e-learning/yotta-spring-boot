package com.xjtu.utils;

import com.xjtu.common.domain.Result;

/**
 * 统一处理成功和失败的信息
 * Created by 18710 on 2017/8/9.
 */
public class ResultUtil {

    /**
     * 成功状态：
     *
     * @param code   成功状态码
     * @param msg    成功回传信息
     * @param object 成功需要返回的对象
     * @return 请求成功状态
     */
    public static Result success(Integer code, String msg, Object object) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

    /**
     * 失败状态：
     *
     * @param code 失败状态码
     * @param msg  失败回传信息，一般为失败原因
     * @return 请求失败状态
     */
    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败状态：
     *
     * @param code 失败状态码
     * @param msg  失败回传信息，一般为失败原因
     * @return 请求失败状态
     */
    public static Result error(Integer code, String msg, Object object) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

}
