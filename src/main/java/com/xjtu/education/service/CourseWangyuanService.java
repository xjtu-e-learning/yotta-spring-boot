package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.education.domain.CourseWangyuan;
import com.xjtu.education.repository.CourseWangyuanRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangkuan
 */
@Service
public class CourseWangyuanService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CourseWangyuanRepository courseWangyuanRepository;

    @Autowired
    DomainRepository domainRepository;

    public Result findDomainByCourseId(Long courseId) {
        CourseWangyuan courseWangyuan = courseWangyuanRepository.findOne(courseId);
        if (courseWangyuan == null) {
            logger.error("网院课程查询失败：不存在该门网院课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getMsg());
        }
        if (courseWangyuan.getCourseWiki() == null || ("").equals(courseWangyuan.getCourseWiki())) {
            logger.error("网院课程查询失败：不存在对应网院课程的维基课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getMsg());
        }
        Domain domain = domainRepository.findByDomainName(courseWangyuan.getCourseWiki());
        Map<String, Object> map = new HashMap<>(2);
        map.put("wangyuan", courseWangyuan);
        map.put("wiki", domain);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), map);
    }

    /**
     * 获取从domainId到courseId的映射
     *
     * @param domainId
     * @return
     */
    public Result findDomainByDomainId(Long domainId) {
        Domain domain = domainRepository.findOne(domainId);
        if (domain == null) {
            logger.error("网院课程查询失败：不存在对应网院课程的维基课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getMsg());
        }
        CourseWangyuan courseWangyuan = courseWangyuanRepository.findByCourseWiki(domain.getDomainName());
        if (courseWangyuan == null) {
            logger.error("网院课程查询失败：不存在该门网院课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getMsg());
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("wangyuan", courseWangyuan);
        map.put("wiki", domain);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), map);
    }

    public Result findDomainByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("网院课程查询失败：不存在对应网院课程的维基课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR_1.getMsg());
        }
        CourseWangyuan courseWangyuan = courseWangyuanRepository.findByCourseWiki(domain.getDomainName());
        if (courseWangyuan == null) {
            logger.error("网院课程查询失败：不存在该门网院课程");
            return ResultUtil.error(ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getCode()
                    , ResultEnum.COURSEWANGYUAN_SEARCH_ERROR.getMsg());
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("wangyuan", courseWangyuan);
        map.put("wiki", domain);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), map);
    }

}
