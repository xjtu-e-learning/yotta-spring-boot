package com.xjtu.data.dao;

import com.xjtu.common.Config;
import com.xjtu.utils.mysqlUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MysqlWrite {
    /**
     * 李军军	2020.11
     * 修改assemble
     *
     * @return
     */
    public static boolean updateAssemble(List<Long> assembleIds,List<String> assembleTexts,List<String> assembleContents) throws InterruptedException {
        for(int i=0;i<assembleIds.size();i++){
            mysqlUtils mysql = new mysqlUtils();
            String sql= "update assemble set assemble_content=?,assemble_text=? where assemble_id=?";
            //System.out.println("修改成功！");
            List<Object> params = new ArrayList<Object>();
            params.add(assembleContents.get(i));
            params.add(assembleTexts.get(i));
            params.add(assembleIds.get(i));
            Thread.currentThread().sleep(10);
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                System.out.println("碎片:"+assembleIds.get(i)+" 修改失败");
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }

        }
        return true;
    }

    public static void storeAssemble(Long assembleId,String assembleContent,String assembleText,Long domainId,Long facetId,Long sourceId) throws Exception {

        mysqlUtils mysql = new mysqlUtils();
        String sql = "insert into " + Config.ASSEMBLE_TABLE + "(assemble_id,assemble_content,assemble_scratch_time,assemble_text,domain_id," +
                "facet_id,source_id,type) "
                + "values(?,?, ?, ?, ?, ?, ?, ?)";

        List<Object> params = new ArrayList<Object>();
        //时间
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前系统时间，也可使用当前时间戳
        String assembleScratchTime = df.format(new Date());
        String type="text";
        params.add(assembleId);
        params.add(assembleContent);
        params.add(assembleScratchTime);
        params.add(assembleText);
        params.add(domainId);
        params.add(facetId);
        params.add(sourceId);
        params.add(type);
        try {
            mysql.addDeleteModify(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }


    }




}
