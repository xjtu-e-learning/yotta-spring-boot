package com.xjtu.topic.extraction;

import java.util.HashSet;

/**
 * Description
 *
 * filter helper
 */
public class FilterUtils {
    private HashSet<String> hashSet = new HashSet<>();

    public void setHashSet() {
        String[] list = {"Template:","/"," ",".",":","（","：","·","_","V","!","~",",",
                "，","#","^","$","@","&","%",";","?","、","[","*","+","(","！","I","|"};
        int length = list.length;
        for (int i = 0; i < length; i++) {
            hashSet.add(list[i]);
        }
    }

    public HashSet<String> getHashSet(){
        return hashSet;
    }

}
