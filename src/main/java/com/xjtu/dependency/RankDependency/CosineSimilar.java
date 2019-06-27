package com.xjtu.dependency.RankDependency;

/**
 * 计算两个文档之间的余弦相似度， 中文文本或英文文本
 * 获得两个主题描述文本的相似度
 * 输入：两个主题的描述文本
 * 输出相似度值
 */

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CosineSimilar {

    public static void main(String[] args) {
//        String doc1 = "A vEB supports the operations of an ordered associative array, which includes the usual associative array operations along with two more order operations, FindNext and FindPrevious:[2]";
//        String doc2 = "The operation FindNext(T, x) that searches for the successor of an element x in a vEB tree proceeds as follows: If x≤T.min then the search is complete, and the answer is T.min. If x>T.max then the next element does not exist, return M. Otherwise, let i = x/√M. If x≤T.children[i].max then the value being searched for is contained in T.children[i] so the search proceeds recursively in T.children[i]. Otherwise, we search for the value i in T.aux. This gives us the index j of the first subtree that contains an element larger than x. The algorithm then returns T.children[j].min. The element found on the children level needs to be composed with the high bits to form a complete next element.";
//
//        double cos = getSimilarityEn(doc1, doc2);
//        System.out.println(cos);

//        String str1="余弦定理算法：doc1 与 doc2 相似度为：0.9954971, 耗时：22mm";
//        String str2="余弦定理算法：doc1 和doc2 相似度为：0.99425095, 用时：33mm";
//        long start=System.currentTimeMillis();
//        double Similarity=getSimilarity(str1, str2);
//        System.out.println("用时:"+(System.currentTimeMillis()-start));
//        System.out.println(Similarity);

    }

    /**
     * 算两个文档之间的余弦相似度：英文文档
     * @param doc1 文档1
     * @param doc2 文档2
     * @return 余弦相似度
     */
    public static double getSimilarityEn(String doc1, String doc2) {
        if (doc1 != null && doc1.trim().length() > 0 && doc2 != null && doc2.trim().length() > 0) {

            Map<String, int[]> AlgorithmMap = new HashMap<>();

            // Lucene Ik Analyzer 中英文分词
            StringReader reader = new StringReader(doc1);
            IKSegmenter ik = new IKSegmenter(reader, true); // 当为true时，分词器进行最大词长切分
            Lexeme lexeme = null;
            try {
                while ((lexeme = ik.next()) != null) {
                    String word = lexeme.getLexemeText();
                    int[] fq = AlgorithmMap.get(word);
                    if (fq != null && fq.length == 2) {
                        fq[0]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 1;
                        fq[1] = 0;
                        AlgorithmMap.put(word, fq);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }

            reader = new StringReader(doc2);
            ik = new IKSegmenter(reader, true); // 当为true时，分词器进行最大词长切分
            lexeme = null;
            try {
                while ((lexeme = ik.next()) != null) {
                    String word = lexeme.getLexemeText();
                    int[] fq = AlgorithmMap.get(word);
                    if (fq != null && fq.length == 2) {
                        fq[1]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 0;
                        fq[1] = 1;
                        AlgorithmMap.put(word, fq);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }

//            for (String word : AlgorithmMap.keySet()) {
//                Log.log(word + "：[" + AlgorithmMap.get(word)[0] + "," + AlgorithmMap.get(word)[1] +"]");
//            }

            Iterator<String> iterator = AlgorithmMap.keySet().iterator();
            double sqdoc1 = 0;
            double sqdoc2 = 0;
            double denominator = 0;
            while (iterator.hasNext()) {
                int[] c = AlgorithmMap.get(iterator.next());
                denominator += c[0] * c[1];
                sqdoc1 += c[0] * c[0];
                sqdoc2 += c[1] * c[1];
            }

            return denominator / Math.sqrt(sqdoc1 * sqdoc2);
        } else {
            throw new NullPointerException(" the Document is null or have not cahrs!!");
        }
    }

    /**
     * 算两个文档之间的余弦相似度：中文文档
     * @param doc1 文档1
     * @param doc2 文档2
     * @return 余弦相似度
     */
    public static double getSimilarity(String doc1, String doc2) {
        if (doc1 != null && doc1.trim().length() > 0 && doc2 != null
                && doc2.trim().length() > 0) {

            Map<Integer, int[]> AlgorithmMap = new HashMap<>();

            //将两个字符串中的中文字符以及出现的总数封装到，AlgorithmMap中
            for (int i = 0; i < doc1.length(); i++) {
                char d1 = doc1.charAt(i);
                if (isHanZi(d1)) {
                    int charIndex = getGB2312Id(d1);
                    if (charIndex != -1) {
                        int[] fq = AlgorithmMap.get(charIndex);
                        if (fq != null && fq.length == 2) {
                            fq[0]++;
                        } else {
                            fq = new int[2];
                            fq[0] = 1;
                            fq[1] = 0;
                            AlgorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }

            for (int i = 0; i < doc2.length(); i++) {
                char d2 = doc2.charAt(i);
                if (isHanZi(d2)) {
                    int charIndex = getGB2312Id(d2);
                    if (charIndex != -1) {
                        int[] fq = AlgorithmMap.get(charIndex);
                        if (fq != null && fq.length == 2) {
                            fq[1]++;
                        } else {
                            fq = new int[2];
                            fq[0] = 0;
                            fq[1] = 1;
                            AlgorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }

            Iterator<Integer> iterator = AlgorithmMap.keySet().iterator();
            double sqdoc1 = 0;
            double sqdoc2 = 0;
            double denominator = 0;
            while (iterator.hasNext()) {
                int[] c = AlgorithmMap.get(iterator.next());
                denominator += c[0] * c[1];
                sqdoc1 += c[0] * c[0];
                sqdoc2 += c[1] * c[1];
            }

            return denominator / Math.sqrt(sqdoc1 * sqdoc2);
        } else {
            throw new NullPointerException(
                    " the Document is null or have not cahrs!!");
        }
    }

    public static boolean isHanZi(char ch) {
        // 判断是否汉字
        return (ch >= 0x4E00 && ch <= 0x9FA5);

    }

    /**
     * 根据输入的Unicode字符，获取它的GB2312编码或者ascii编码，
     *
     * @param ch 输入的GB2312中文字符或者ASCII字符(128个)
     * @return ch在GB2312中的位置，-1表示该字符不认识
     */
    public static short getGB2312Id(char ch) {
        try {
            byte[] buffer = Character.toString(ch).getBytes("GB2312");
            if (buffer.length != 2) {
                // 正常情况下buffer应该是两个字节，否则说明ch不属于GB2312编码，故返回'?'，此时说明不认识该字符
                return -1;
            }
            int b0 = (int) (buffer[0] & 0x0FF) - 161; // 编码从A1开始，因此减去0xA1=161
            int b1 = (int) (buffer[1] & 0x0FF) - 161; // 第一个字符和最后一个字符没有汉字，因此每个区只收16*6-2=94个汉字
            return (short) (b0 * 94 + b1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
