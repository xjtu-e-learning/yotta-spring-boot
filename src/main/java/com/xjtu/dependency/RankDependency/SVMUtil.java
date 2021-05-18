package com.xjtu.dependency.RankDependency;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: makexin
 * @Date: 2021/5/1714:20
 */

public class SVMUtil {

//    private static String svm_model_file = "D:/java_workspace/save_svm_model/model.md"; //本地路径
    //服务器路径
    private static String svm_model_file = "E:/dependency_svm_model/model.md";

    public static void train(int num, List<Float> sim,List<Float> asy, List<Float> simOfName, List<Double> lab)
    {
        svm_problem sp = new svm_problem();
        svm_node[][] x = new svm_node[num][3];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < 3; j++) {
                x[i][j] = new svm_node();
            }
        }
        double[] labels = new double[num];
        for (int i = 0; i<num; i++)
        {
            x[i][0].index = 1;
            x[i][0].value = sim.get(i);
            x[i][1].index = 2;
            x[i][1].value = asy.get(i);
            x[i][2].index = 3;
            x[i][2].value = simOfName.get(i);
            labels[i] = lab.get(i);
        }
        sp.x = x;
        sp.y = labels;
        sp.l = num;
        svm_parameter prm = new svm_parameter();
        prm.svm_type = svm_parameter.C_SVC;
        prm.kernel_type = svm_parameter.RBF;
        prm.C = 1000;
        prm.eps = 0.0000001;
        prm.gamma = 10;
        prm.probability = 1;
        prm.cache_size=1024;
        System.out.println("Param Check " + (svm.svm_check_parameter(sp, prm)==null));
        double err = 0.0;
        svm_model model = svm.svm_train(sp, prm);           //训练分类
        try {
            svm.svm_save_model(svm_model_file, model);

            for(int i = 0; i<num; i++)
            {
                double predictValue = svm.svm_predict(model, x[i]);
                err +=  Math.abs(predictValue - labels[i]);
            }
            System.out.println("err = " + err/labels.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Double> predict(int num, List<Float> sim, List<Float> asy, List<Float> simOfName)
    {
        List<Double> testLabels = new ArrayList<>();
        try {
            svm_model model = svm.svm_load_model(svm_model_file);
            svm_node[][] test = new svm_node[num][3];
            for (int i = 0; i < num; i++) {
                for (int j = 0; j < 3; j++) {
                    test[i][j] = new svm_node();
                }
            }
            for (int i = 0; i<num; i++)
            {
                test[i][0].index = 1;
                test[i][0].value = sim.get(i);
                test[i][1].index = 2;
                test[i][1].value = asy.get(i);
                test[i][2].index = 3;
                test[i][2].value = simOfName.get(i);
            }
            for(int i = 0; i<num; i++)
            {
                double predictValue = svm.svm_predict(model, test[i]);
                testLabels.add(predictValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testLabels;
    }
}
