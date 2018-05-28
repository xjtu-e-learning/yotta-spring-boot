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

    //学科
    SUBJECT_INSERT_ERROR(108, "学科信息插入失败：学科名不存在或者为空"),
    SUBJECT_INSERT_ERROR_1(109, "学科信息插入失败：插入已经存在的学科"),
    SUBJECT_INSERT_ERROR_2(110, "学科信息插入失败：数据库插入语句失败"),
    SUBJECT_DELETE_ERROR(111, "学科删除失败"),
    SUBJECT_UPDATE_ERROR(112, "学科更新失败"),
    SUBJECT_SEARCH_ERROR(113, "学科查询失败：没有学科信息记录"),
    SUBJECT_SEARCH_ERROR_1(114, "学科分页查询失败：没有该条学科信息记录"),
    SUBJECT_SEARCH_ERROR_2(115, "学科分页查询失败：查询的页数超过最大页数"),

    //课程
    DOMAIN_INSERT_ERROR(116, "课程信息插入失败：课程名不存在或者为空"),
    DOMAIN_INSERT_ERROR_1(117, "课程信息插入失败：插入已经存在的课程"),
    DOMAIN_INSERT_ERROR_2(118, "课程信息插入失败：数据库插入语句失败"),
    DOMAIN_DELETE_ERROR(119, "课程删除失败"),
    DOMAIN_UPDATE_ERROR(120, "课程更新失败：更新语句执行失败"),
    DOMAIN_UPDATE_ERROR_1(121, "课程更新失败：课程名不存在或者为空"),
    DOMAIN_SEARCH_ERROR(122, "课程查询失败：没有课程信息记录"),
    DOMAIN_SEARCH_ERROR_1(123, "课程分页查询失败：没有课程信息记录"),
    DOMAIN_SEARCH_ERROR_2(124, "课程分页查询失败：查询的页数超过最大页数"),

    //主题
    TOPIC_INSERT_ERROR(125, "主题信息插入失败：主题名不存在或者为空"),
    TOPIC_INSERT_ERROR_1(126, "主题信息插入失败：插入主题已经存在"),
    TOPIC_INSERT_ERROR_2(127, "主题信息插入失败：数据库插入语句失败"),
    TOPIC_DELETE_ERROR(128, "主题删除失败"),
    TOPIC_UPDATE_ERROR(129, "主题更新失败：更新语句执行失败"),
    TOPIC_UPDATE_ERROR_1(130, "主题更新失败：主题名不存在或者为空"),
    TOPIC_UPDATE_ERROR_2(131, "主题更新失败：课程不存在"),
    TOPIC_SEARCH_ERROR(132, "主题查询失败：没有指定主题"),
    TOPIC_SEARCH_ERROR_1(133, "主题查询失败：课程下没有主题信息记录"),
    TOPIC_SEARCH_ERROR_2(134, "主题查询失败：没有指定课程"),
    TOPIC_DELETE_ERROR_1(135, "主题删除失败:没有主题对应的课程"),
    TOPIC_DELETE_ERROR_2(136, "主题删除失败:主题数据不存在"),
    TOPIC_UPDATE_ERROR_3(137, "主题更新失败：原主题不存在"),
    TOPIC_INSERT_ERROR_3(138, "主题信息插入失败：没有对应的课程"),


    //分面
    FACET_INSERT_ERROR(139, "分面信息插入失败：分面名不存在或者为空"),
    FACET_INSERT_ERROR_1(140, "分面信息插入失败：分面插入语句执行失败"),
    FACET_DELETE_ERROR(141,"分面信息删除失败：分面不存在"),
    FACET_DELETE_ERROR_1(141,"分面信息删除失败：删除语句执行失败"),
    FACET_DELETE_ERROR_2(179, "分面信息删除失败：对应课程不存在"),
    FACET_DELETE_ERROR_3(180, "分面信息删除失败：对应主题不存在"),
    FACET_DELETE_ERROR_4(181, "分面信息删除失败：删除语句执行失败"),
    FACET_UPDATE_ERROR(142,"分面更新失败：更新语句执行失败"),
    FACET_UPDATE_ERROR_1(160,"分面更新失败：对应课程不存在"),
    FACET_UPDATE_ERROR_2(161,"分面更新失败：对应主题不存在"),
    FACET_UPDATE_ERROR_3(162,"分面更新失败：原分面不存在"),
    FACET_SEARCH_ERROR(143,"分面查询失败：查询语句执行失败"),
    FACET_SEARCH_ERROR_1(144,"分面分页查询失败：没有数据源记录"),
    FACET_SEARCH_ERROR_2(145,"分面分页查询失败：查询的页数超过最大页数"),
    FACET_SEARCH_ERROR_3(147,"分面查询失败：对应课程不存在"),
    FACET_SEARCH_ERROR_4(148,"分面查询失败：对应主题不存在"),
    FACET_SEARCH_ERROR_5(149,"分面查询失败：对应课程和主题下没有分面"),
    FACET_SEARCH_ERROR_6(150,"分面查询失败：对应课程和主题下没有对应分面"),
    FACET_SEARCH_ERROR_7(151,"分面查询失败：对应课程、主题下以及分面下没有子分面"),
    FACET_INSERT_ERROR_2(152, "分面信息插入失败：对应课程不存在"),
    FACET_INSERT_ERROR_3(153, "分面信息插入失败：对应主题不存在"),
    FACET_INSERT_ERROR_4(159, "分面信息插入失败：对应父分面不存在"),



    //碎片
    Assemble_SEARCH_ERROR(163,"碎片查询失败：对应课程不存在"),
    Assemble_SEARCH_ERROR_1(164,"碎片查询失败：对应主题不存在"),
    Assemble_SEARCH_ERROR_2(165,"碎片查询失败：对应分面不存在"),
    Assemble_SEARCH_ERROR_3(174,"碎片查询失败：查询语句执行失败"),
    Assemble_INSERT_ERROR(166,"碎片插入失败：插入语句执行失败"),
    Assemble_INSERT_ERROR_1(167,"碎片插入失败：碎片内容为空"),
    Assemble_INSERT_ERROR_2(168,"碎片插入失败：对应课程不存在"),
    Assemble_INSERT_ERROR_3(169,"碎片插入失败：对应主题不存在"),
    Assemble_INSERT_ERROR_4(170,"碎片插入失败：对应分面不存在"),
    Assemble_INSERT_ERROR_5(171,"碎片插入失败：碎片暂存表不存在该碎片"),
    Assemble_INSERT_ERROR_6(172,"碎片插入失败：对应数据源不存在"),
    Assemble_UPDATE_ERROR(173,"碎片更新失败：碎片id不存在"),
    Assemble_UPDATE_ERROR_1(174,"碎片更新失败：更新语句执行失败"),
    Assemble_DELETE_ERROR(175,"碎片删除失败：删除语句执行失败"),



    //用户登录
    LOGIN_ERROR(154, "登录失败：用户不存在"),

    //依赖关系
    DEPENDENCY_SEARCH_ERROR(155, "主题依赖关系查询失败：没有课程信息记录"),
    DEPENDENCY_SEARCH_ERROR_1(156, "主题依赖关系查询失败：该课程下没有主题依赖关系记录"),
    DEPENDENCY_SEARCH_ERROR_2(157, "主题依赖关系生成失败：gexf文件生成失败"),
    DEPENDENCY_SEARCH_ERROR_3(176, "主题依赖关系生成失败：起始或终止主题不存在"),
    DEPENDENCY_SEARCH_ERROR_4(178, "主题依赖关系查询失败：查询语句执行失败"),
    DEPENDENCY_INSERT_ERROR(177, "主题依赖关系插入失败:插入语句执行失败"),
    DEPENDENCY_DELETE_ERROR(179, "主题依赖关系删除失败:没有课程信息记录"),
    DEPENDENCY_DELETE_ERROR_1(180, "主题依赖关系删除失败：删除语句执行失败"),

    //课程数据统计
    STATISTICS_SEARCH_ERROR(158, "词频查询失败：中文分词失败")
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
