package com.xjtu.assemble.algorithm;

import com.mayabot.nlp.fasttext.FastText;
import com.mayabot.nlp.fasttext.ScoreLabelPair;
import com.mayabot.nlp.fasttext.args.InputArgs;
import com.mayabot.nlp.fasttext.loss.LossName;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.springframework.stereotype.Component;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AssembleMatch {
    private String path = "E:\\Yotta";

    public boolean isAssembleFacetMatch(String facet, String assemble) {
        boolean result = false;
//        FastText model = loadModel("G:\\JavaProjects\\Yotta");
        assemble = textSeg(assemble);
        List<ScoreLabelPair> modelOutput = testModel(assemble);
        List<String> modelOutputLabel = new ArrayList<>();
        for (int i = 0; i < modelOutput.size(); i++) {
            modelOutputLabel.add(modelOutput.get(i).getLabel());
        }
        facet = "__label__" + facet;
        for (int i = 0; i < modelOutputLabel.size(); i++) {
            if (facet.equals(modelOutputLabel.get(i))) {
                result = true;
            }
        }
        return result;
    }

    public List<String> assignFacetForassemble(String assemble) {
        List<String> result = new ArrayList<>();
//        FastText model = loadModel("G:\\JavaProjects\\Yotta");
        assemble = textSeg(assemble);
        List<ScoreLabelPair> modelOutput = testModel(assemble);
        List<String> modelOutputLabel = new ArrayList<>();
        for (int i = 0; i < modelOutput.size(); i++) {
            modelOutputLabel.add(modelOutput.get(i).getLabel().replace("__label__", ""));
        }
        return modelOutputLabel;
    }


    public void trainModel(){
        File trainfile = new File(path + "\\train.csv");
        InputArgs inputArgs = new InputArgs();
        inputArgs.setLoss(LossName.softmax);
        inputArgs.setLr(0.1);
        inputArgs.setDim(200);
        inputArgs.setEpoch(20);
        FastText model = FastText.trainSupervised(trainfile, inputArgs);
        try{
            model.saveModel(path + "\\data.model");
        }catch(Exception e){
        }

    }

    public FastText loadModel() {
        FastText model = FastText.Companion.loadModel(new File(path + "\\data.model"), false);
        return model;
    }


    public List<ScoreLabelPair> testModel(String assemble) {
        FastText model = loadModel();
        List<ScoreLabelPair> result = model.predict(Arrays.asList(assemble.split(" ")), 3, 0);
        return result;
    }

    public String textSeg(String text) {
        Result result = NlpAnalysis.parse(text);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < result.size(); i++) {
            sb.append(result.get(i).getName());
            if (i != result.size() - 1) sb.append(" ");
        }

        return sb.toString();
    }


    /**
     * 直接运行此文件可测试分面碎片是否匹配
     */
    public static void main(String[] args) {
        AssembleMatch am = new AssembleMatch();
        am.trainModel();
        List<String> result = am.assignFacetForassemble("一个电池电阻组成直流电路周围空间中坡印亭矢量方向向外说明流出");
        System.out.println(result.toString());
        boolean isMatch = am.isAssembleFacetMatch("定义", "一个电池电阻组成直流电路周围空间中坡印亭矢量方向向外说明流出");
        System.out.println(isMatch);
    }
}
