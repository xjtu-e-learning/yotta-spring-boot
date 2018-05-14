package com.xjtu.spider.spiders.webmagic.bean;

import java.util.List;

/**
 * @author yuanhao
 * @date 2017/12/19 22:43
 */
public class FragmentContent {

    List<String> fragments;
    List<String> fragmentsPureText;

    @Override
    public String toString() {
        return "FragmentContent{" +
                "fragments=" + fragments +
                ", fragmentsPureText=" + fragmentsPureText +
                '}';
    }

    public List<String> getFragments() {
        return fragments;
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }

    public List<String> getFragmentsPureText() {
        return fragmentsPureText;
    }

    public void setFragmentsPureText(List<String> fragmentsPureText) {
        this.fragmentsPureText = fragmentsPureText;
    }

    public FragmentContent() {

    }

    public FragmentContent(List<String> fragments, List<String> fragmentsPureText) {

        this.fragments = fragments;
        this.fragmentsPureText = fragmentsPureText;
    }
}
