package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.education.domain.AssembleEvaluation;
import com.xjtu.education.repository.AssembleEvaluationRepository;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author yangkuan
 */
@Service
public class AssembleEvaluationService {

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
    public Result saveAssembleQuality(Long userId, Long assembleId, int value) {
        Date currentTime = new Date();
        //查找之前用户是否评价过碎片
        AssembleEvaluation assembleEvaluation = assembleEvaluationRepository.findByAssembleIdAndUserId(assembleId, userId);
        if (assembleEvaluation == null) {
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
}
