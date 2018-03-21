package com.xjtu.dependency.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.domain.Dependency;
import com.xjtu.dependency.domain.DependencyContainName;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理主题依赖关系数据
 * @author yangkuan
 * @date 2018/03/21 12:46
 * */
@Service
public class DependencyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DependencyRepository dependencyRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    DomainRepository domainRepository;


    /**
     * 通过主课程名，获取该课程下的主题依赖关系
     * @param domainName
     * @return
     * */
    public Result findDependenciesByDomainName(String domainName){
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if(domain==null){
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);
        //查询错误
        if(dependencies.size()==0){
            logger.error("主题依赖关系查询失败：该课程下没有主题依赖关系记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_1.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_1.getMsg());
        }
        List<DependencyContainName> dependencyContainNames = new ArrayList<>();
        for(Dependency dependency:dependencies){
            DependencyContainName dependencyContainName = new DependencyContainName(dependency);
            //获取主题名
            String startTopicName = topicRepository.findByTopicIdAndDomainId(dependency.getStartTopicId(),domainId).getTopicName();
            String endTopicName = topicRepository.findByTopicIdAndDomainId(dependency.getEndTopicId(),domainId).getTopicName();
            //设置主题名
            dependencyContainName.setStartTopicName(startTopicName);
            dependencyContainName.setEndTopicName(endTopicName);
            dependencyContainNames.add(dependencyContainName);
        }
        logger.info("主题依赖关系查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);
    }

}
