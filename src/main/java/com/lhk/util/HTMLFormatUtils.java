package com.lhk.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class HTMLFormatUtils {
    // 定义script
    /**  正则表达式{或<script[^>]*?>[//s//S]*?<///script> 清除所有的script标签以及内容 */
    private final static Pattern SCRIPT_PATTERN = Pattern.compile("<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>");

    // 定义style
    /** 正则表达式{或<style[^>]*?>[//s//S]*?<///style> 清除所有的style标签以及内容 */
    private final static Pattern STYLE_PATTERN = Pattern.compile("<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>");

    private final static Pattern ENTER_PATTERN = Pattern.compile("(\\\\r|\r|\\\\n|\n)");

    private final static Pattern HTML_PATTERN_PAGE = Pattern.compile("(</p>|<br/>|<br>|</br>)");
    private final static Pattern MORE_ENTER_PAGE = Pattern.compile("[\n]+");
    /** 正则表达式，清除html标签，html的注解以及html的空格换行换页符 */
    private final static Pattern HTML_PATTERN_1 = Pattern.compile("<[^<>]+>");
    private final static Pattern HTML_PATTERN_2 = Pattern.compile("<!--[^<>]+-->");
    private final static Pattern TITLE_PATTERN = Pattern.compile("^【.*.】.*");


    /**
     * 过滤文本中的所有 html标签
     *
     * @param htmlStr
     * @return
     */
    public static String filterHtml(String htmlStr) {
        if (StringUtils.isBlank(htmlStr)) {
            return "";
        }

        htmlStr = SCRIPT_PATTERN.matcher(htmlStr).replaceAll("");
        htmlStr = STYLE_PATTERN.matcher(htmlStr).replaceAll("");
        htmlStr = ENTER_PATTERN.matcher(htmlStr).replaceAll("\n");
        htmlStr = HTML_PATTERN_PAGE.matcher(htmlStr).replaceAll("\n");
        htmlStr = MORE_ENTER_PAGE.matcher(htmlStr).replaceAll("\r\n");
        htmlStr = HTML_PATTERN_1.matcher(htmlStr).replaceAll("");
        htmlStr = HTML_PATTERN_2.matcher(htmlStr).replaceAll("");
        return htmlStr;
    }

    public static String filterEmptyValue(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    public static String clearTimeHeader(String value) {
        int index = 0;
        boolean existSpecialCharacter = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '0' && '9' >= c) {
                index++;
            } else if (c == '.' || c == ':' || c == '-' || c == ' ' || c == '/') {
                index++;
                existSpecialCharacter = true;
            } else {
                break;
            }
        }
        if (index >= 5 || existSpecialCharacter) {
            return value.substring(index);
        }
        return value;
    }

    public static String clearHTMLTitle(String title) {
        if (TITLE_PATTERN.matcher(title).find()) {
            String tempTitle = title.substring(title.indexOf('【') + 1, title.indexOf('】'));
            if (tempTitle.length() > 5) {
                return tempTitle;
            }
        }
        return title;
    }
}
