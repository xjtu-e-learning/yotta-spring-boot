package com.xjtu.data.service;

import com.xjtu.assemble.dao.AssembleDAO;
import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.assemble.repository.TemporaryAssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.data.dao.MysqlWrite;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private TemporaryAssembleRepository temporaryAssembleRepository;

    @Autowired
    private AssembleDAO assembleDAO;

    @Value("${image.location}")
    private String imagePath;

    @Value("${image.remote}")
    private String remotePath;



    public Result assembleTransfer(Long subjectId) throws Exception {


        List<Domain> domains = domainRepository.findBySubjectId(subjectId);
        for (Domain domain : domains) {
            System.out.println(domain.getDomainName());}
        for (Domain domain : domains) {
            //System.out.println(domain.getDomainName());
            List<Topic> topics=topicRepository.findByDomainId(domain.getDomainId());
            if(topics.size()==0){
                System.out.println("课程:" + domain.getDomainName() + "下没有主题");
                continue;
            }
            for (Topic topic : topics) {
                List<Facet> facets=facetRepository.findByTopicId(topic.getTopicId());
                if(facets.size()==0) {
                    //System.out.println("课程:" + domain.getDomainName() + "主题：" + topic.getTopicName() + "没有分面");
                    continue;
                }
                for (Facet facet : facets) {
                    List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
                    if (assembles.size() == 0) {
                       // System.out.println("课程:" + domain.getDomainName() + ",主题：" + topic.getTopicName() + ",分面：" + facet.getFacetName() + "下没有碎片");
                        continue;
                    }
                    List<Long> assembleIds = new ArrayList<Long>();
                    List<String> assembleTexts = new ArrayList<String>();
                    List<String> assembleContents = new ArrayList<String>();
                    for (Assemble assemble : assembles) {
                        //存储这个碎片
                        //MysqlWrite.storeAssemble(assemble.getAssembleId(),assemble.getAssembleContent(),assemble.getAssembleText(),assemble.getDomainId(),assemble.getFacetId(),assemble.getSourceId());
                        //修改这个碎片
                        //System.out.println("碎片id："+assemble.getAssembleId());
                        //System.out.println(assemble.getAssembleContent());

                        assembleIds.add(assemble.getAssembleId());
                        assembleTexts.add(assemble.getAssembleText());
                        assembleContents.add(assemble.getAssembleContent());

                        //System.out.println(assemble.getAssembleId());
                    }
                    MysqlWrite.updateAssemble(assembleIds,assembleTexts,assembleContents);
                    assembles.clear();
                    assembleIds.clear();
                    assembleContents.clear();
                    assembleTexts.clear();
                }

            }
            System.out.println("**************课程："+domain.getDomainName()+"下的碎片迁移完成");
        }


//        List<Long> assembleId = new ArrayList<Long>();
//        assembleId.add(0L);
//        assembleId.add(1L);
//
//        List<String> assembleTexts= new ArrayList<String>();
//        assembleTexts.add(0,"22修改后的0000000");
//        assembleTexts.add(1,"22修改后的1111111");
//        List<String> assembleContents= new ArrayList<String>();
//        assembleContents.add(0,"22修改后的0000000");
//        assembleContents.add(1,"22修改后的1111111");
//        MysqlWrite.updateAssemble(assembleId,assembleTexts,assembleContents);

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"ok!");
    }
}
