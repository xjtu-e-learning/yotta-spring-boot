package com.xjtu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanhao
 * @date 2018/3/31 11:06
 */
public class WordSegmentationUtils {

    private static final Logger logger = LoggerFactory.getLogger(WordSegmentationUtils.class);

    public static void main(String[] args) {
        String doc1 = "A vEB supports the operations of an ordered associative array, which includes the usual associative array operations along with two more order operations, FindNext and FindPrevious:[2]";
        String doc2 = "The operation FindNext(T, x) that searches for the successor of an element x in a vEB tree proceeds as follows: If x≤T.min then the search is complete, and the answer is T.min. If x>T.max then the next element does not exist, return M. Otherwise, let i = x/√M. If x≤T.children[i].max then the value being searched for is contained in T.children[i] so the search proceeds recursively in T.children[i]. Otherwise, we search for the value i in T.aux. This gives us the index j of the first subtree that contains an element larger than x. The algorithm then returns T.children[j].min. The element found on the children level needs to be composed with the high bits to form a complete next element.";

        IKSegmenterTest(doc2);
    }

    public static void IKSegmenterTest(String doc1) {
        // 词频
        Map<String, Integer> wordfre = new HashMap<>();
        // Lucene Ik Analyzer 中文分词
        StringReader reader = new StringReader(doc1);
        IKSegmenter ik = new IKSegmenter(reader, true); // 当为true时，分词器进行最大词长切分
        Lexeme lexeme = null;
        try {
            while ((lexeme = ik.next()) != null) {
                String word = lexeme.getLexemeText();
                if (!wordfre.containsKey(word)) {
                    wordfre.put(word, 1);
                } else {
                    wordfre.put(word, wordfre.get(word) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        for (String word : wordfre.keySet()) {
            logger.info(word + "：" + wordfre.get(word));
        }
    }

}
