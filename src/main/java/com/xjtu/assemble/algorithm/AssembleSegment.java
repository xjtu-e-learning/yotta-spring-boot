package com.xjtu.assemble.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssembleSegment {
    public List<String> assembleSegment(String assemble) {
        String[] assembleArray = assemble.split("\n");
        List<String> assembleList = new ArrayList<>(Arrays.asList(assembleArray));
        return assembleList;
    }

    public static void main(String[] args) {
        AssembleSegment as = new AssembleSegment();
        try{
            System.out.println(as.assembleSegment(new String("test\ntest".getBytes("UTF-8"))));
        }catch (Exception e){

        }
    }
}
