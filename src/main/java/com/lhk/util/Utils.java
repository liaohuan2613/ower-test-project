package com.lhk.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static int n = 10;
    private static long[] x;
    private static double[] y;

    static {
        x = new long[n];
        y = new double[n];
        int a = 1;
        for (int i = 0; i < n; ++i) {
            x[i] = a;
            y[i] = x[i];
            a *= 10;
        }
    }

    public static DateTimeFormatter dayFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd");
    }

    public static DateTimeFormatter dayFormatter2() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static LocalDate parseDay(String dayString) {
        return LocalDate.parse(dayString, dayFormatter());
    }

    public static LocalDate parseDay2(String dayString) {
        return LocalDate.parse(dayString, dayFormatter2());
    }

    public static String formatDay(LocalDate day) {
        return day.format(dayFormatter());
    }

    public static String formatDay2(LocalDate day) {
        return day.format(dayFormatter2());
    }

    public static LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate getDay(String dayString) {

        LocalDate today = LocalDate.now();
        try {
            today = Utils.parseDay2(dayString);
        } catch (Exception e) {
            log.error("日期格式非法：" + dayString);
        }
        return today;

    }

    public static <K, T> void group(Map<K, List<T>> map, K key, T item) {
        List<T> items = map.get(key);
        if (items == null) {
            map.put(key, items = new ArrayList<>());
        }
        if (item != null) items.add(item);
    }

    public static <K, T> void groupToSet(Map<K, Set<T>> map, K key, T item) {
        Set<T> items = map.get(key);
        if (items == null) {
            map.put(key, items = new HashSet<>());
        }
        items.add(item);
    }

//    public static <K, T> void groupToSetByMultiKey(MultiKeyMap map, T item, K... keys) {
//        Set<T> items = (Set<T>) map.get(keys);
//        if(items==null) {
//            map.put(new MultiKey(keys), items = new HashSet<>());
//        }
//        items.add(item);
//    }

    public static <K, T> void sum(Map<K, Long> map, K key, long count) {
        Long sum = map.get(key);
        map.put(key, sum == null ? count : sum + count);
    }

//    public static <K, T> void sumByMultiKey(MultiKeyMap map, long count, K... keys) {
//        Long sum = (Long) map.get(keys);
//        map.put(new MultiKey(keys), sum==null ? count : sum + count);
//    }

    public static String[] toArray(List<String> list) {
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    public static Double percentage(Long denominator, Long numerator) {
        return (denominator == null || denominator == 0)
                ? null
                : ((numerator == null || numerator == 0)
                ? 0
                : numerator * 100.0 / denominator);
    }

    public static Double ratio(Long denominator, Long numerator) {
        return (denominator == null || denominator == 0)
                ? null
                : ((numerator == null || numerator == 0)
                ? 0
                : numerator * 1.0 / denominator);
    }

    public static double level = 0.95;

    public static double filterNaN(double value) {
        return Double.isNaN(value) ? Double.MAX_VALUE : value;
    }

    public static Map<String, double[]> convert(Map<String, List<Double>> source) {
        Map<String, double[]> target = new HashMap<>();

        source.keySet().stream().forEach(key -> {
            double[] array = source.get(key).stream().mapToDouble(Double::doubleValue).toArray();
            target.put(key, array);
        });

        return target;
    }

    public static String concat(char separator, String... sts) {

        if (sts.length == 0) return "";

        StringBuilder sb = new StringBuilder();

        sb.append(sts[0]);

        for (int i = 1; i < sts.length; ++i) {
            sb.append(separator);
            sb.append(sts[i]);
        }

        return sb.toString();
    }

    public static String concat(String... sts) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sts.length; ++i) {
            sb.append(sts[i]);
        }

        return sb.toString();
    }

    public static String concat(String[] template, String[] variables) {
        int n = template.length, m = variables.length, j = 0;

        StringBuilder sb = new StringBuilder();

        sb.append(template[0] == null ? variables[j++] : template[0]);

        for (int i = 1; i < n; ++i) {
            sb.append(":");
            sb.append(template[i] == null ? variables[j++] : template[i]);
        }

        return sb.toString();
    }

    public static StringBuilder concatsb(String... sts) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sts.length; ++i) {
            sb.append(sts[i]);
        }

        return sb;
    }

    public static String getTdidFromUserIdentifier(String userIdentifier) {
        return userIdentifier.substring(
                userIdentifier.lastIndexOf(":") + 1
        );
    }

    public static String getCategoryFromUserIdentifier(String userIdentifier) {
        return userIdentifier.substring(
                StringUtils.ordinalIndexOf(userIdentifier, ":", 1) + 1,
                StringUtils.ordinalIndexOf(userIdentifier, ":", 2)
        );
    }

    public static Double reserveDigits(Double value, int digits) {
        if (value == null) return null;

        Long a = null;
        Double b = null;
        if (digits < n) {
            a = x[digits];
            b = y[digits];
        } else {
            b = Math.pow(10, digits);
            a = Math.round(b);
        }
        return Math.round(value * a) / b;
    }

    public static <V> Map<String, V> mapById(List<V> objects, Class<V> clazz, String keyMethodName) {
        Map<String, V> map = new HashMap();

        try {
            for (V object : objects) {
                String id = (String) clazz.getMethod("getId").invoke(object);
                map.put(id, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Long getNotNullCountOr0(Map<String, ?> countMap, String key) {
        Long count = (Long) countMap.get(key);
        return count == null ? 0 : count;
    }

    public static boolean isAnyEmpty(String... items) {
        for (String item : items) {
            if (item == null || item.isEmpty()) return true;
        }
        return false;
    }

    public static <V> Map<String, List<V>> group(List<V> objects, Function<V, String> function) {
        Map<String, List<V>> map = new HashMap();

        try {
            for (V object : objects) {
                String id = (String) function.apply(object);
                List<V> list = map.get(id);
                if (list == null) {
                    map.put(id, list = new ArrayList<>());
                }
                list.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, Double> sortMap(Map<String, Double> originalMap, int limit) {
        return originalMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        Map<String, String> config = new HashMap<>();
        try {
            config = yaml.load(new FileInputStream("C:\\Users\\liaoh\\Documents\\WeChat Files\\liaohuan2613\\FileStorage\\File\\2020-02\\model.yml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(config.get("trainNum"));
        System.out.println(config.get("hasCutword"));
    }
}
