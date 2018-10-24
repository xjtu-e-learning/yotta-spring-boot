package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.domain.HotTopic;
import com.xjtu.education.repository.HotTopicRepository;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 热度主题业务逻辑
 *
 * @author yangkuan
 * @date 2018/10/24
 */
@Service
public class HotTopicService {

    @Autowired
    HotTopicRepository hotTopicRepository;

    /**
     * 保存热度主题
     *
     * @param domainId
     * @param hotTopics
     * @return
     */
    public Result saveHotTopicsByDomainId(Long domainId, String hotTopics) {
        HotTopic hotTopic = hotTopicRepository.findByDomainId(domainId);
        if (hotTopic == null) {
            hotTopic = new HotTopic();
            hotTopic.setCreatedTime(new Date());
            hotTopic.setModifiedTime(new Date());
            hotTopic.setDomainId(domainId);
            hotTopic.setHotTopics(hotTopics);
            hotTopicRepository.save(hotTopic);
        } else {
            hotTopicRepository.updateByDomainId(domainId, hotTopics, new Date());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "热度主题保存成功");
    }

    /**
     * 查询热度主题
     *
     * @param domainId
     * @return
     */
    public Result findHotTopicsByDomainId(Long domainId) {
        HotTopic hotTopic = hotTopicRepository.findByDomainId(domainId);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), hotTopic);
    }
}
