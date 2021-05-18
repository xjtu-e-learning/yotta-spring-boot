package com.xjtu.dependency.RankDependency;

import com.hankcs.hanlp.HanLP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: makexin
 * @Date: 2021/3/229:47
 */
public class SimilarityUtil {
//    static {
//
//        CustomDictionary.add("子类");
//
//        CustomDictionary.add("父类");
//
//    }


    /**

     * 获得两个句子的相似度

     *

     * @param sentence1

     * @param sentence2

     * @return

     */

    public static double getSimilarity(String sentence1, String sentence2) {

        List<String> sent1Words = getSplitWords(sentence1);

//        System.out.println(sent1Words);

        List<String> sent2Words = getSplitWords(sentence2);

//        System.out.println(sent2Words);

        List<String> allWords = mergeList(sent1Words, sent2Words);

        int[] statistic1 = statistic(allWords, sent1Words);

        int[] statistic2 = statistic(allWords, sent2Words);

        double dividend = 0;

        double divisor1 = 0;

        double divisor2 = 0;

        for (int i = 0; i < statistic1.length; i++) {

            dividend += statistic1[i] * statistic2[i];

            divisor1 += Math.pow(statistic1[i], 2);

            divisor2 += Math.pow(statistic2[i], 2);

        }

        return dividend / (Math.sqrt(divisor1) * Math.sqrt(divisor2));

    }

    private static int[] statistic(List<String> allWords, List<String> sentWords) {

        int[] result = new int[allWords.size()];

        for (int i = 0; i < allWords.size(); i++) {

            result[i] = Collections.frequency(sentWords, allWords.get(i));

        }

        return result;

    }

    private static List<String> mergeList(List<String> list1, List<String> list2) {

        List<String> result = new ArrayList<>();

        result.addAll(list1);

        result.addAll(list2);

        return result.stream().distinct().collect(Collectors.toList());

    }

    public static List<String> getSplitWords(String sentence) {

//        // 去除掉html标签
//
//        sentence = Jsoup.parse(sentence.replace(" ","")).body().text();

        // 标点符号会被单独分为一个Term，去除之

        return HanLP.segment(sentence).stream().map(a -> a.word).filter(s -> !"`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？ ".contains(s)).collect(Collectors.toList());

    }
}
