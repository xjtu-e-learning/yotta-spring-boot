package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.domain.AssembleEvaluation;
import com.xjtu.education.repository.AssembleEvaluationRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangkuan
 */
@Service
public class AssembleEvaluationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AssembleEvaluationRepository assembleEvaluationRepository;


    /**
     * 保存评价
     *
     * @param userId
     * @param assembleId
     * @param value
     * @return
     */
    public Result saveAssembleEvaluation(Long userId, Long assembleId, int value) {
        Date currentTime = new Date();
        //查找之前用户是否评价过碎片
        AssembleEvaluation assembleEvaluation = assembleEvaluationRepository.findByAssembleIdAndUserId(assembleId, userId);
        if (assembleEvaluation == null) {
            assembleEvaluation = new AssembleEvaluation();
            assembleEvaluation.setAssembleId(assembleId);
            assembleEvaluation.setUserId(userId);
            assembleEvaluation.setValue(value);
            assembleEvaluation.setCreatedTime(currentTime);
            assembleEvaluation.setModifiedTime(currentTime);
            assembleEvaluationRepository.save(assembleEvaluation);
        } else if (value != assembleEvaluation.getValue()) {
            assembleEvaluation.setModifiedTime(currentTime);
            assembleEvaluation.setValue(value);
            assembleEvaluationRepository.save(assembleEvaluation);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "评价保存成功");
    }

    /**
     * 根据碎片id，查询该碎片下的用户评价（赞数/踩数）
     *
     * @param assembleId
     * @return
     */
    public Result findAssembleEvaluationStatistics(Long assembleId) {
        List<AssembleEvaluation> assembleEvaluations = assembleEvaluationRepository.findByAssembleId(assembleId);
        Map<String, Integer> result = new HashMap<>(2);
        if (assembleEvaluations == null || assembleEvaluations.size() == 0) {
            result.put("positiveCnt", 0);
            result.put("negativeCnt", 0);
        } else {
            int positiveCnt = 0;
            int negativeCnt = 0;
            for (AssembleEvaluation assembleEvaluation : assembleEvaluations) {
                if (assembleEvaluation.getValue().equals(1)) {
                    positiveCnt++;
                } else if (assembleEvaluation.getValue().equals(-1)) {
                    negativeCnt++;
                }
            }
            result.put("positiveCnt", positiveCnt);
            result.put("negativeCnt", negativeCnt);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result);
    }
}
