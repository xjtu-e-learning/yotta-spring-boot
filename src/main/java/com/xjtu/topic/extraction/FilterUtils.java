package com.xjtu.topic.extraction;

import java.util.HashSet;

/**
 * Description
 * <p>
 * filter helper
 */
public class FilterUtils {
    private HashSet<String> symbolSet = new HashSet<>();
    private HashSet<String> objectSet = new HashSet<>();

    public void setHashSet() {
        String[] symbolList = {"Template:", "User", "Portal", "Web", "/", " ", ".", ":", "（", "：", "·", "_", "V", "!", "~", ",",
                "，", "#", "^", "$", "@", "&", "%", ";", "?", "、", "[", "*", "+", "(", "！", "I", "|"};
        int length = symbolList.length;
        for (int i = 0; i < length; i++) {
            symbolSet.add(symbolList[i]);
        }

        String[] objectList = {"Intel", "Facebook", "Twitter", "Windows", "Adobe", "Microsoft", "Google", "Amazon", "Alpha", "ABBYY", "racle", "Apache", "ISO", "XML", "AMD", "AX", "AJAX", ".NET", "LaTeX", "JSON", "UNIX", "Beta","X",
                "SIG", "Net", "Steam","ANSI", "Kotlin", "EBay","IBM", "CSS", "SPSS", "Wii", "Online", "Eclipse", "CAD", "AWT", "Atom", "zip", "OS", "Wiki", "GUI", "Qt", "Game", "POSIX", "Free", "的", "章", "厂", "党", "霸", "魔","HTML",
                "游戏", "鬼", "辐", "之", "软件", "升阳", "国际", "互联网", "基金", "资料库", "成人", "世界", "智慧", "驾驶", "实验室", "刺客", "华硕", "英雄", "中华", "公安", "浏览器", "黑客", "审查", "系列", "卡内基","网路","网络","神","爱",
                "虚拟", "中国", "引擎", "直播", "山寨", "杀", "大赛", "传奇", "显卡", "软体", "公司", "全国", "播", "联合国", "机动", "实践", "程式", "战", "超级", "冒险", "电影", "360", "性爱", "数码", "性交", "输入","王国","log","邮","bus",
                "魅", "用户","完美","JPEG","骑士","维基","博客","竞技","园","堂","刀","OL","Dj","表情","Script","时代","武","绝地","逃","3D","城市","ABC"};
        length = objectList.length;
        for (int i = 0; i < length; i++) {
            objectSet.add(objectList[i]);
        }

    }

    public HashSet<String> getSymbolSet() {
        return symbolSet;
    }

    public HashSet<String> getObjectSet() {
        return objectSet;
    }
}
