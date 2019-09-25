package com.lhk.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhk.domain.AutoTagArticle;
import com.lhk.domain.BlockWeightTag;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsAuditApplication {
    private static final Gson GSON = new Gson();
    private static final Type TAG_LIST_TYPE = new TypeToken<List<BlockWeightTag>>() {
    }.getType();

    private static List<String> tagCodeList = Arrays.asList("CLS_20103030", "CLS_20103006", "CLS_20102001", "CLS_20101010", "CLS_20102009",
            "CLS_20103029", "CLS_20103023", "CLS_20101011", "CLS_20103028", "CLS_20103013", "CLS_20103019", "CLS_20102006", "CLS_20103025",
            "CLS_20103024", "CLS_20103027", "CLS_20103026", "CLS_20103003", "CLS_20103010", "CLS_20102005", "CLS_20103015", "CLS_20101009",
            "CLS_20103020", "CLS_20103018", "CLS_20103001", "CLS_20101002", "CLS_20101005", "CLS_20103007", "CLS_20103004", "CLS_20103002",
            "CLS_20103005", "CLS_20102004", "CLS_20102008", "CLS_20103022", "CLS_20103021", "CLS_20102007", "CLS_20101008", "CLS_20103008",
            "CLS_20102002", "CLS_20103012", "CLS_20102003", "CLS_20101001", "CLS_20101003", "CLS_20101004", "CLS_20101006", "CLS_20103011",
            "CLS_20101007", "CLS_20103009", "CLS_10103010", "CLS_10103004", "CLS_10103007", "CLS_10103009", "CLS_10103008", "CLS_10103005",
            "CLS_10103003", "CLS_10103002", "CLS_10103006", "CLS_10103011", "CLS_10103017", "CLS_10103012", "CLS_10103021", "CLS_10103022",
            "CLS_10103013", "CLS_10103019", "CLS_10103015", "CLS_10103016", "CLS_10103020", "CLS_10103014", "CLS_10103018", "CLS_10108011");

    public static void main(String[] args) {
        JdbcTemplate fhlJdbcTemplate = JdbcTemplateApplication.getFhlJdbcTemplate();
        List<AutoTagArticle> maps = fhlJdbcTemplate.query("SELECT * FROM `newsAudit`.`news_audit` WHERE `publish_time` LIKE '%2019-09-09%'",
                new BeanPropertyRowMapper(AutoTagArticle.class));
        maps.forEach(map -> {
            List<BlockWeightTag> tagList = GSON.fromJson(map.getAutoTags(), TAG_LIST_TYPE);
            Set<String> codeSet = new HashSet<>();
            for (BlockWeightTag tag : tagList) {
                if (tagCodeList.contains(tag.getTagCode()) && tag.getWeight() > 80) {
                    codeSet.add(tag.getTagCode());
                }
            }
            if (codeSet.size() > 0) {
                System.out.println(map.getItemId() + "\t" + map.getTitle().replaceAll("[\r\n]", "") + "\t" + map.getContent().replaceAll("[\r\n]", "") + "\t" + codeSet);
            }
        });
    }
}
