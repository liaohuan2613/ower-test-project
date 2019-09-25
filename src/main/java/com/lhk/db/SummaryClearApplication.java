package com.lhk.db;

import com.lhk.thread.SummaryClearThread;
import com.lhk.thread.SummaryClearThread2;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SummaryClearApplication {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        int startSkip = 0;
        int size = 1000;
        int endSize = 40000;
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getPocJdbcTemplate();

        while (startSkip < endSize) {
            executor.execute(new SummaryClearThread2(startSkip, jdbcTemplate));
            startSkip += size;
        }
    }
}
