package com.lhk;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lhk.db.JdbcTemplateApplication;
import com.lhk.domain.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class TemplateApplication {

    public static void main(String[] args) throws IOException {
        String json = "{\"user_name\":\"bflee\",\"id_number\":\"123456\"}";
        System.out.println(JSONObject.parseObject(json, Test.class));


//        JdbcTemplate oracleJdbcTemplate = JdbcTemplateApplication.getOracleJdbcTemplate();
//        System.out.println(oracleJdbcTemplate.queryForList("select count(*) from block"));
    }

}
