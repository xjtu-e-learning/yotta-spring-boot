package com.xjtu.dependency.RankDependency;

import com.xjtu.dependency.domain.Dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: makexin
 * @Date: 2021/3/2121:15
 */
public class DFSvisit {

    //将认知关系转化为链表形式链接
    public HashMap<Long, List<Dependency>> changeRelation(List<Dependency> dependencies)
    {
        HashMap<Long, List<Dependency>> relations = new HashMap<>();
        for(Dependency d : dependencies)
        {
            if(!relations.containsKey(d.getStartTopicId()))
            {
                List<Dependency> templist = new ArrayList<>();
                templist.add(d);
                relations.put(d.getStartTopicId(), templist);
            }
            else
            {
                List<Dependency> templist = relations.get(d.getStartTopicId());
                templist.add(d);
                relations.put(d.getStartTopicId(), templist);
            }
        }
        System.out.println("将认知关系转换为链表");
        return relations;
    }

    //删除前向边
    public HashMap<Long, List<Dependency>> relationProcess(HashMap<Long, List<Dependency>> relations)
    {
        List<List<Long>> resultPaths = new ArrayList<>();
        HashMap<Long, List<Dependency>> resultRelations = new HashMap<>();
        List<List<Long>> path = new ArrayList<>();
        for(Long key : relations.keySet())
        {
            List<Long> temppath = new ArrayList<>();
            temppath.add(key);
            path.add(temppath);
            List<Dependency> templist = new ArrayList<>();
            resultRelations.put(key, templist);
        }
        System.out.println("path size: " + path.size());
        while (path.size() > 0 && path.get(0).size()==1)
        {
            List<Long> currPath = path.get(0);
            path = new ArrayList<List<Long>>(path.subList(1,path.size()));
            Long currTopic = currPath.get(currPath.size()-1);
            if(!relations.containsKey(currTopic))
            {
                resultPaths.add(currPath);
            }
            else
            {
                List<Long> tmp = new ArrayList<>();
                tmp.addAll(currPath);
                for(Dependency key : relations.get(currTopic))
                {
                    if(currPath.contains(key.getEndTopicId()))
                    {
                        continue;
                    }
                    else
                    {
                        tmp.add(key.getEndTopicId());
                    }
                }
                path.add(tmp);
            }
//            System.out.println("第一个while, path size: " + path.size());
        }
        //找出前向边
        HashMap<Long, List<Long>> falseFlag = new HashMap<>();

        for(Long key : relations.keySet())
        {
            for(Dependency endTopicId: relations.get(key))
            {
                boolean flag = true;
                for(List<Long> arr : path)
                {
                    if(arr.contains(key)&& arr.indexOf(key)!=0 && arr.contains(endTopicId.getEndTopicId()) && arr.indexOf(endTopicId.getEndTopicId())!=0)
                    {
                        if (falseFlag.containsKey(arr.get(0)))
                        {
                            List<Long> l = falseFlag.get(arr.get(0));
                            l.add(endTopicId.getEndTopicId());
                            falseFlag.put(arr.get(0), l);
                        }
                        else
                        {
                            List<Long> l = new ArrayList<>();
                            l.add(endTopicId.getEndTopicId());
                            falseFlag.put(arr.get(0), l);
                        }
                        //修改为，删掉已有路径里，重复较长的那条边，即path里entopicid那条边
//                        flag = false;
////                        System.out.println("去除一条边");
//                        break;
                    }
                }
//                if (flag)
//                {
//                    List<Dependency> list = resultRelations.get(key);
//                    list.add(endTopicId);
//                    resultRelations.put(key, list);
//
//                }
            }

        }
        for(Long key : relations.keySet())
        {
            for(Dependency endTopicId: relations.get(key))
            {
                if (falseFlag.containsKey(key))
                {
                    if (falseFlag.get(key).contains(endTopicId.getEndTopicId()))
                    {
                        continue;
                    }
                }
                List<Dependency> list = resultRelations.get(key);
                list.add(endTopicId);
                resultRelations.put(key, list);
            }
        }
        return resultRelations;
    }
}
