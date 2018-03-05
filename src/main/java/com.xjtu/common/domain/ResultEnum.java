package com.xjtu.common.domain;

/**
 * 枚举管理所有失败原因
 * Created by yuanhao on 2017/8/9.
 */
public enum ResultEnum {
    SUCCESS(200, "成功"),
    UNKONW_ERROR(-1, "未知错误"),

    // 数据源
    SOURCE_INSERT_ERROR(100, "数据源插入失败：数据源名不存在或者为空"),
    SOURCE_INSERT_ERROR_1(101, "数据源插入失败：插入已经存在的数据源"),
    SOURCE_INSERT_ERROR_2(102, "数据源插入失败：数据库插入语句失败"),
    SOURCE_DELETE_ERROR(103, "数据源删除失败"),
    SOURCE_UPDATE_ERROR(104, "数据源更新失败"),
    SOURCE_SEARCH_ERROR(105, "数据源查询失败：没有数据源记录"),
    SOURCE_SEARCH_ERROR_1(106, "数据源分页查询失败：没有数据源记录"),
    SOURCE_SEARCH_ERROR_2(107, "数据源分页查询失败：查询的页数超过最大页数"),

    ;

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
