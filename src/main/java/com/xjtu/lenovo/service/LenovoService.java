package com.xjtu.lenovo.service;

import com.csvreader.CsvReader;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.facet.service.FacetService;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.topic.service.TopicService;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
public class LenovoService {
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private FacetRepository facetRepository;
    @Autowired
    private TopicService topicService;
    @Autowired
    private FacetService facetService;
    public static void main(String[] args) {
        LenovoService lenovoService=new LenovoService();
        lenovoService.findKgInTopicAndFacet();
    }

    public Result findKgInTopicAndFacet(){
        ArrayList<String[]> csvList =readCsvFile("C:\\Users\\ljj\\Desktop\\联想\\联想学科知识点_初中数学.csv");
        ArrayList<String[]> firstKgList=new ArrayList<String[]>();
        ArrayList<String[]> secondKgList=new ArrayList<String[]>();
        ArrayList<String[]> thirdKgList=new ArrayList<String[]>();
        for(int row=0;row<csvList.size();row++){
            if(csvList.get(row)[8].equals("1")){
                firstKgList.add(csvList.get(row));
            }
            else if(csvList.get(row)[8].equals("2")){
                secondKgList.add(csvList.get(row));
            }
            else if(csvList.get(row)[8].equals("3")){
                thirdKgList.add(csvList.get(row));
            }
//            System.out.println("-----------------");
//            //打印每一行的数据
//            System.out.print(csvList.get(row)[0]+",");
//            System.out.print(csvList.get(row)[1]+",");
//            System.out.print(csvList.get(row)[2]+",");
//            System.out.println(csvList.get(row)[3]+",");
            //如果第一列（即姓名列）包含lisa，则打印出lisa的年龄
        }
        System.out.println("一级知识点有"+firstKgList.size());
        System.out.println("二级知识点有"+secondKgList.size());
        System.out.println("三级知识点有"+thirdKgList.size());

        List<Topic> topicList1=topicRepository.findByDomainName("七年级数学");
        List<Topic> topicList2=topicRepository.findByDomainName("八年级数学");
        List<Topic> topicList3=topicRepository.findByDomainName("九年级数学");
        System.out.println("七年级数学的主题个数："+topicList1.size());
        System.out.println("八年级数学的主题个数："+topicList2.size());
        System.out.println("九年级数学的主题个数："+topicList3.size());
        List<Topic> topicList=new ArrayList<Topic>();
        topicList.addAll(topicList1);
        topicList.addAll(topicList2);
        topicList.addAll(topicList3);
        List<Facet> facetList=new ArrayList<Facet>();
        for(Topic topic:topicList){
            List<Facet> facetListTmp=facetRepository.findByTopicId(topic.getTopicId());
            facetList.addAll(facetListTmp);
        }
        System.out.println("初中数学共有主题："+topicList.size());
        System.out.println("初中数学共有分面："+facetList.size());

        int coutTopic=0;
        int coutFacet=0;
        for (int row = 0; row < firstKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(firstKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
//                    System.out.println(firstKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                    continue;
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(firstKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
                    continue;
//                    System.out.println(firstKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("一级知识点总数为："+firstKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("一级知识点总数为："+firstKgList.size()+",与分面名一致的个数："+coutFacet);



        coutTopic=0;
        coutFacet=0;
        for (int row = 0; row < secondKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(secondKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
     //               System.out.println(secondKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(secondKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
  //                  System.out.println(secondKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("二级知识点总数为："+secondKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("二级知识点总数为："+secondKgList.size()+",与分面名一致的个数："+coutFacet);


        coutTopic=0;
        coutFacet=0;
        for (int row = 0; row < thirdKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(thirdKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
              //      System.out.println(thirdKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(thirdKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
                //    System.out.println(thirdKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("三级知识点总数为："+thirdKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("三级知识点总数为："+thirdKgList.size()+",与分面名一致的个数："+coutFacet);


        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), csvList);

    }

    public Result findKgInTopicAndFacet1(){
        ArrayList<String[]> csvList =readCsvFile("C:\\Users\\ljj\\Desktop\\联想\\联想学科知识点_初中物理.csv");
        ArrayList<String[]> firstKgList=new ArrayList<String[]>();
        ArrayList<String[]> secondKgList=new ArrayList<String[]>();
        ArrayList<String[]> thirdKgList=new ArrayList<String[]>();
        for(int row=0;row<csvList.size();row++){
            if(csvList.get(row)[8].equals("1")){
                firstKgList.add(csvList.get(row));
            }
            else if(csvList.get(row)[8].equals("2")){
                secondKgList.add(csvList.get(row));
            }
            else if(csvList.get(row)[8].equals("3")){
                thirdKgList.add(csvList.get(row));
            }
//            System.out.println("-----------------");
//            //打印每一行的数据
//            System.out.print(csvList.get(row)[0]+",");
//            System.out.print(csvList.get(row)[1]+",");
//            System.out.print(csvList.get(row)[2]+",");
//            System.out.println(csvList.get(row)[3]+",");
            //如果第一列（即姓名列）包含lisa，则打印出lisa的年龄
        }
        System.out.println("一级知识点有"+firstKgList.size());
        System.out.println("二级知识点有"+secondKgList.size());
        System.out.println("三级知识点有"+thirdKgList.size());

        List<Topic> topicList1=topicRepository.findByDomainName("八年级物理");
        List<Topic> topicList2=topicRepository.findByDomainName("九年级物理");
        System.out.println("八年级物理的主题个数："+topicList1.size());
        System.out.println("九年级物理的主题个数："+topicList2.size());
        List<Topic> topicList=new ArrayList<Topic>();
        topicList.addAll(topicList1);
        topicList.addAll(topicList2);
        List<Facet> facetList=new ArrayList<Facet>();
        for(Topic topic:topicList){
            List<Facet> facetListTmp=facetRepository.findByTopicId(topic.getTopicId());
            facetList.addAll(facetListTmp);
        }
        System.out.println("初中物理共有主题："+topicList.size());
        System.out.println("初中物理共有分面："+facetList.size());

        int coutTopic=0;
        int coutFacet=0;
        for (int row = 0; row < firstKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(firstKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
//                    System.out.println(firstKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                    continue;
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(firstKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
                    continue;
//                    System.out.println(firstKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("一级知识点总数为："+firstKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("一级知识点总数为："+firstKgList.size()+",与分面名一致的个数："+coutFacet);



        coutTopic=0;
        coutFacet=0;
        for (int row = 0; row < secondKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(secondKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
                    //               System.out.println(secondKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(secondKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
                    //                  System.out.println(secondKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("二级知识点总数为："+secondKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("二级知识点总数为："+secondKgList.size()+",与分面名一致的个数："+coutFacet);


        coutTopic=0;
        coutFacet=0;
        for (int row = 0; row < thirdKgList.size(); row++) {
            //与主题名匹配的个数
            int coutTopicOld=coutTopic;
            for(int i=0;i<topicList.size();i++){
                if(thirdKgList.get(row)[1].equals(topicList.get(i).getTopicName())){
                    coutTopic++;
                    //      System.out.println(thirdKgList.get(row)[1]+"=="+topicList.get(i).getTopicName());
                }
            }
            if(coutTopicOld!=coutTopic)
                continue;
            for(int i=0;i<facetList.size();i++){
                if(thirdKgList.get(row)[1].equals(facetList.get(i).getFacetName())){
                    coutFacet++;
                    //    System.out.println(thirdKgList.get(row)[1]+"=="+facetList.get(i).getFacetName());
                }
            }
        }
        System.out.println("三级知识点总数为："+thirdKgList.size()+",与主题名一致的个数："+coutTopic);
        System.out.println("三级知识点总数为："+thirdKgList.size()+",与分面名一致的个数："+coutFacet);

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), csvList);

    }

    public ArrayList<String[]> readCsvFile(String filePath){
        ArrayList<String[]> csvList = new ArrayList<String[]>();
        try {

            CsvReader reader = new CsvReader(filePath,',', Charset.forName("utf-8"));
    //          reader.readHeaders(); //跳过表头,不跳可以注释掉

            while(reader.readRecord()){
                csvList.add(reader.getValues()); //按行读取，并把每一行的数据添加到list集合
            }
            reader.close();
            System.out.println("读取的行数："+csvList.size());

//            for(int row=0;row<csvList.size();row++){
//                System.out.println("-----------------");
//                //打印每一行的数据
//                System.out.print(csvList.get(row)[0]+",");
//                System.out.print(csvList.get(row)[1]+",");
//                System.out.print(csvList.get(row)[2]+",");
//                System.out.println(csvList.get(row)[3]+",");
//                //如果第一列（即姓名列）包含lisa，则打印出lisa的年龄
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return csvList;
    }


}
