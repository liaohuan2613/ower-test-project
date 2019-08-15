package com.lhk.regex;

import com.lhk.mysql.JdbcTemplateApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class RegexRunner {
    public static void main(String[] args) throws Exception {
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getJdbcTemplate();
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\工作目录\\正则匹配\\正则记录.txt"));
        String line;
        List<EventRegex> eventRegexList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            int thisIndex = count % 3;
            if (thisIndex == 0) {
                List<Map<String, Object>> mapList = new ArrayList<>();
                try {
                    mapList = jdbcTemplate.queryForList("select distinct e.code code, e.name name " +
                            " from event e left join event_det ed on e.`code` = ed.`code` " +
                            " where ed.`value` = '" + line + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, Object> map = new HashMap<>();
                if (mapList.size() > 0) {
                    map = mapList.get(0);
                } else {
                    System.out.println();
                }
                eventRegexList.add(new EventRegex(map.get("code").toString(), map.get("name").toString(), line,
                        new ArrayList<>(), new ArrayList<>()));
                count++;
            } else if (thisIndex == 1) {
                if ("".equals(line.trim())) {
                    count++;
                } else {
                    eventRegexList.get(eventRegexList.size() - 1).getTopTwoWords().add(line);
                }
            } else if (thisIndex == 2) {
                if ("".equals(line.trim())) {
                    count++;
                } else {
                    eventRegexList.get(eventRegexList.size() - 1).getLastTwoWords().add(line);
                }
            }
        }
        Set<String> simSameEventSet = new HashSet<>();
        Set<String> simOtherEventSet = new HashSet<>();
        for (int i = 0; i < eventRegexList.size(); i++) {
            for (int j = i; j < eventRegexList.size(); j++) {
                for (String topTwoWord : eventRegexList.get(i).getTopTwoWords()) {
                    for (String compareTopTwoWord : eventRegexList.get(j).getTopTwoWords()) {
                        if (compareWord(topTwoWord, compareTopTwoWord)) {
                            for (String lastTwoWord : eventRegexList.get(i).getLastTwoWords()) {
                                for (String compareLastTwoWord : eventRegexList.get(j).getLastTwoWords()) {
                                    if (compareWord(lastTwoWord, compareLastTwoWord)) {
                                        String eventCode = eventRegexList.get(i).getEventCode();
                                        String eventName = eventRegexList.get(i).getEventName();
                                        String regex = eventRegexList.get(i).getEventRegex();
                                        String compareEventCode = eventRegexList.get(i).getEventCode();
                                        String compareEventName = eventRegexList.get(i).getEventName();
                                        String compareRegex = eventRegexList.get(j).getEventRegex();
                                        String regexCompareRegex = eventCode + "\t" + eventName + "\t" + regex + "\t"
                                                + compareEventCode + "\t" + compareEventName + "\t" + compareRegex + "\r\n";
                                        String compareRegexRegex = compareEventCode + "\t" + compareEventName + "\t"
                                                + compareRegex + "\t" + eventCode + "\t" + eventName + "\t" + regex + "\r\n";
                                        if (!regex.equals(compareRegex) && !simSameEventSet.contains(regexCompareRegex)
                                                && !simSameEventSet.contains(compareRegexRegex) && !simOtherEventSet.contains(regexCompareRegex)
                                                && !simOtherEventSet.contains(compareRegexRegex)) {
                                            if (compareEventCode.equals(eventCode)) {
                                                simSameEventSet.add(regexCompareRegex);
                                            } else {
                                                simOtherEventSet.add(regexCompareRegex);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        simSameEventSet.forEach(System.out::println);
        System.out.println("\n");
        simOtherEventSet.forEach(System.out::println);
        System.out.println(simOtherEventSet);

        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Administrator\\Desktop\\result.txt"));
        for (String simStr : simSameEventSet) {
            bw.write(simStr);
        }

        bw.write("\r\n");
        bw.write("\r\n");

        for (String simStr : simOtherEventSet) {
            bw.write(simStr);
        }
        bw.flush();
        bw.close();

    }

    private static boolean compareWord(String firstWord, String compareWord) {
        boolean flag = true;
        for (int i = 0; i < firstWord.length(); i++) {
            if (compareWord.length() <= 1) {
                System.out.println(compareWord);
            }
            if ('_' != firstWord.charAt(i) && '_' != compareWord.charAt(i) && firstWord.charAt(i) != compareWord.charAt(i)) {
                flag = false;
            }
        }
        return flag;
    }
}
