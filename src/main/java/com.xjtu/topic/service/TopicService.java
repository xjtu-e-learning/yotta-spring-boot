package com.xjtu.topic.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 处理topic主题数据
 * @author yangkuan
 * @date 2018/03/09 15:15
 */

@Service
public class TopicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    /**
     * 插入主题信息
     * @param topic 需要插入的主题
     * @return 插入结果
     * */
    public Result insertTopic(Topic topic){

        String topicName = topic.getTopicName();
        Long domainId = topic.getDomainId();

        //插入主题的主题名必须存在且不能为空
        if(topicName==null||topicName.equals("")||topicName.length()==0){
            logger.error("主题信息插入失败：主题名不存在或者为空");
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR.getCode(),ResultEnum.TOPIC_INSERT_ERROR.getMsg());
        }
        //保证插入主题不存在数据库中
        else if(topicRepository.findByTopicNameAndDomainId(topicName, domainId)==null){
            Topic topicInsert = topicRepository.save(topic);
            if(topicInsert==null){
                logger.error("主题信息插入失败：数据库插入语句失败");
                return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_2.getCode(),ResultEnum.TOPIC_INSERT_ERROR_2.getMsg());
            }
            else {
                logger.info("插入主题信息成功");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicInsert);
            }
        }
        //主题信息已经在数据库中
        logger.error("主题信息插入失败：插入主题已经存在");
        return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_1.getCode(), ResultEnum.TOPIC_INSERT_ERROR_1.getMsg());
    }

    /**
     * 插入主题信息
     * @param domainName 课程名
     * @param topicName 主题名
     * @return 插入结果
     * */
    public Result insertDomainByNameAndTopicName(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("主题信息插入失败：没有对应的课程");
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_3.getCode(), ResultEnum.TOPIC_INSERT_ERROR_3.getMsg());
        }
        Topic topic = new Topic();
        topic.setDomainId(domain.getDomainId());
        topic.setTopicName(topicName);
        return insertTopic(topic);
    }

    /**
     * 删除主题信息：根据主题Id
     * @param topicId 主题Id
     * @return 删除结果
     */
    public Result deleteTopic(Long topicId){
        try {
            topicRepository.delete(topicId);
            logger.info("主题删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题删除成功");
        }
        catch (Exception err){
            logger.error("主题删除失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }

    /**
     * 删除主题信息：根据课程名和主题名进行删除
     * @param topicName 主题名
     * @param domainName 课程名
     * @return 删除结果
     */
    public Result deleteDomainByNameAndTopicName(String topicName, String domainName){
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            if(domain==null){
                logger.error("主题删除失败：没有主题对应的课程");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_1.getCode(),ResultEnum.TOPIC_DELETE_ERROR_1.getMsg());
            }

            Topic topic = topicRepository.findByTopicNameAndDomainId(topicName, domain.getDomainId());
            if(topic == null){
                logger.error("主题名删除失败：主题数据不存在");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(),ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
            }
            topicRepository.deleteByTopicNameAndDomainId(topicName, domain.getDomainId());
            logger.info("主题删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题删除成功");
        }
        catch (Exception err){
            logger.error("主题名删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(),ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 更新主题：根据主题名
     * @param oldTopicName 旧主题名
     * @param newTopicName 新主题名
     * @param newDomainName 新课程名
     * @return 更新结果
     */
    public Result updateTopicByName(String oldTopicName, String newTopicName, String newDomainName){
        if(newTopicName==null||newTopicName.equals("")||newTopicName.length()==0){
            logger.error("主题名更新失败：新主题名不存在或为空");
            return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_1.getCode(),ResultEnum.TOPIC_UPDATE_ERROR_1.getMsg());
        }
        try {
            Domain domain = domainRepository.findByDomainName(newDomainName);
            if(domain==null){
                logger.error("主题名更新失败：课程不存在");
                return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_2.getCode(),ResultEnum.TOPIC_UPDATE_ERROR_2.getMsg());
            }
            List<Topic> topics = topicRepository.findByTopicName(oldTopicName);
            if(topics.size()<1){
                logger.error("主题名更新失败：原主题不存在");
                return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_3.getCode(),ResultEnum.TOPIC_UPDATE_ERROR_3.getMsg());
            }
            topicRepository.updateByTopicName(oldTopicName, newTopicName, domain.getDomainId());
            logger.info("主题名更新成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"主题名更新成功");
        }
        catch (Exception err){
            logger.error("主题名更新失败：更新语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR.getCode(),ResultEnum.TOPIC_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 查询主题：根据课程名
     * @param domainName 新课程名
     * @return 查询结果
     */
    public Result findTopicsByDomainName(String domainName){
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        if(topics.size()>0){
            logger.info("主题查询成功");
            topics.forEach(topic -> logger.info("查询结果为：" + topic.toString()));
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topics);
        }
        else {
            logger.error("主题查询失败：该课程下没有主题记录");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(),ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg());
        }
    }

}