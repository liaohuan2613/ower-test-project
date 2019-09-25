package com.lhk.db;

import com.lhk.thread.HotCountThread;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HotSpotApplication {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        LocalDate startDate = LocalDate.parse("2017-06-20", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse("2019-08-29", DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1);
        String endDateFormat = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getPocJdbcTemplate();
        String startDateFormat = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        while (!startDateFormat.equals(endDateFormat)) {
            executor.execute(new HotCountThread(startDateFormat, jdbcTemplate));
            startDate = startDate.plusDays(1);
            startDateFormat = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
