package com.lhk.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexRunner {
    private static Pattern pattern = Pattern.compile("\\\\(.*.\\\\)");
    public static void main(String[] args) {
        String str = "(资产|股权)价值.{0,5}降低";
        Matcher matcher = pattern.matcher(str);
        int groupCount = matcher.groupCount();
        if (groupCount > 0) {
            String group = matcher.group(1);
        }

        List<String> strGroup = new ArrayList<>();
        strGroup.add("资产价值.....降低");
        strGroup.add("股权价值.....降低");
        strGroup.add("资产价值降低");
        strGroup.add("股权价值降低");
        strGroup.add("资产价值.降低");
        strGroup.add("股权价值.降低");
        strGroup.add("资产价值..降低");
        strGroup.add("股权价值..降低");
    }
}
