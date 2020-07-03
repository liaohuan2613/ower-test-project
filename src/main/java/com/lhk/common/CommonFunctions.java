package com.lhk.common;

import com.lhk.util.MathUtil;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonFunctions {

    public final static ThreadLocal<JdbcTemplate> jdbcTemplateThreadLocal = new ThreadLocal<>();
    public final static ThreadLocal<MongoTemplate> mongoTemplateThreadLocal = new ThreadLocal<>();
    public final static ThreadLocal<RestTemplate> restTemplateThreadLocal = new ThreadLocal<>();

    public static List<List<Map.Entry<String, Double>>> formatPredictList(List<Map<String, Double>> predictList) {
        List<List<Map.Entry<String, Double>>> formatList = new ArrayList<>();
        for (Map<String, Double> predict : predictList) {
            double avg = 1.0 / predict.size();
            List<Map.Entry<String, Double>> entryList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : predict.entrySet()) {
                if (avg < entry.getValue()) {
                    entryList.add(entry);
                }
            }
            entryList.sort((o1, o2) -> {
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                } else {
                    return -1;
                }
            });
            formatList.add(entryList);
        }
        return formatList;
    }

    public static Map<String, Object> postResult(String api, Map<String, Object> requestMap, RestTemplate restTemplate) {
        try {
            URI tagUri = UriComponentsBuilder.fromUriString(api).build().encode().toUri();
            if (restTemplate.postForEntity(tagUri, requestMap, Object.class).getBody() instanceof Map) {
                Map<String, Object> objectMap = (Map<String, Object>) restTemplate.postForEntity(tagUri, requestMap, Object.class).getBody();
                if (objectMap.get("result") instanceof Map) {
                    return (Map<String, Object>) objectMap.get("result");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>(1);
    }

    public static List<Object> postResultList(String api, Map<String, Object> requestMap, RestTemplate restTemplate) {
        try {
            URI tagUri = UriComponentsBuilder.fromUriString(api).build().encode().toUri();
            if (restTemplate.postForEntity(tagUri, requestMap, Object.class).getBody() instanceof Map) {
                Map<String, Object> objectMap = (Map<String, Object>) restTemplate.postForEntity(tagUri, requestMap, Object.class).getBody();
                if (objectMap.get("result") instanceof List) {
                    return (List<Object>) objectMap.get("result");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void mergeList(List<Document> rootList, List<String> marketTextList, List<List<Map.Entry<String, Double>>> marketEntryList,
                                 List<String> columnTextList, List<List<Map.Entry<String, Double>>> columnEntryList) {
        for (int i = 0; i < rootList.size(); i++) {
            Map<String, Object> tempMap = rootList.get(i);
            tempMap.put("marketWordSegment", marketTextList.get(i));
            tempMap.put("columnWordSegment", columnTextList.get(i));

            List<String> marketCategories = new ArrayList<>();
            for (Map.Entry<String, Double> entry : marketEntryList.get(i)) {
                String valueStr = String.format("%.0f%%", MathUtil.roundTo(entry.getValue(), 2) * 100);
                String keyStr = entry.getKey().replace("__label__", "");
                if (marketCategories.isEmpty()) {
                    tempMap.put("marketCategory", keyStr);
                }
                marketCategories.add(keyStr + " " + valueStr);
            }
            tempMap.put("marketCategories", marketCategories);


            List<String> columnCategories = new ArrayList<>();
            for (Map.Entry<String, Double> entry : columnEntryList.get(i)) {
                String valueStr = String.format("%.0f%%", MathUtil.roundTo(entry.getValue(), 2) * 100);
                String keyStr = entry.getKey().replace("__label__", "");
                if (columnCategories.isEmpty()) {
                    tempMap.put("columnCategory", keyStr);
                }
                columnCategories.add(keyStr + " " + valueStr);
            }
            tempMap.put("columnCategories", columnCategories);
        }
    }
}
