package com.lhk.tag;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.lhk.db.JdbcTemplateApplication.getZGNewsJdbcTemplate;

public class StockSyncTest {

    public static void main(String[] args) {
        JdbcTemplate zgNewsJdbcTemplate = getZGNewsJdbcTemplate();
        List<SwStock> swStocks = zgNewsJdbcTemplate.query("select * from sw_stock", new BeanPropertyRowMapper(SwStock.class));
        Map<String, String> codeNameMap = swStocks.parallelStream().collect(Collectors.toMap(SwStock::getStockCode, SwStock::getStockName));
        Set<String> usedNamesSet = new HashSet<>();
        Map<String, Set<String>> removeMap = new HashMap<>();
        for (SwStock s : swStocks) {
            String stockCode = s.getStockCode();
            if (!StringUtils.isBlank(s.getUsedNames())) {
                for (Map.Entry<String, String> codeName : codeNameMap.entrySet()) {
                    if (!stockCode.equals(codeName.getKey()) && s.getUsedNames().contains(codeName.getValue())) {
                        Set<String> removeSet = removeMap.computeIfAbsent(stockCode, k -> new HashSet<>());
                        removeSet.add(codeName.getValue());
                    }
                }

                String[] usedNameGroup = s.getUsedNames().split(";");
                for (String usedName : usedNameGroup) {
                    for (String usedNames : usedNamesSet) {
                        if (usedNames.contains(usedName)) {
                            Set<String> removeSet = removeMap.computeIfAbsent(stockCode, k -> new HashSet<>());
                            removeSet.add(usedName);
                        }
                    }
                }
                usedNamesSet.add(s.getUsedNames());
            }
        }

        removeMap.forEach((k, v) -> System.out.println("key: " + k + ", value: " + v));
    }
}
