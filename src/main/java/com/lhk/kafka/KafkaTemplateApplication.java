package com.lhk.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaTemplateApplication {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 50, 0 , TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    private static AtomicBoolean flag = new AtomicBoolean(true);

    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", "c:/tmp/test/kafka_client_jaas.conf");
        readKafkaMsg();
    }

    public static void readAndSendMsg() {
        System.setProperty("java.security.auth.login.config", "c:/tmp/test/kafka_client_jaas.conf");
        KafkaConsumer<String, String> kafkaListener = getKafkaListener();
        kafkaListener.subscribe(Collections.singletonList("origin-owl"));
        KafkaTemplate<String, String> kafkaTemplate = getKafkaTemplate();
        while (flag.get()) {
            ConsumerRecords<String, String> consumerRecords = kafkaListener.poll(Duration.of(1000, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                kafkaTemplate.send("single-origin-owl", record.value());
            }
        }
    }

    public static void readKafkaMsg() {
        KafkaConsumer<String, String> kafkaListener = getKafkaListener();
        kafkaListener.subscribe(Collections.singletonList("test3"));
        while (flag.get()) {
            long startTime = System.currentTimeMillis();
            if (executor.getActiveCount() < executor.getCorePoolSize() - 1) {
                ConsumerRecords<String, String> consumerRecords = kafkaListener.poll(Duration.of(1, ChronoUnit.SECONDS));
                System.out.println("fetch time is " + (System.currentTimeMillis() - startTime));
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    executor.execute(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(record.value());
                    });
                }
            }
        }
    }

    public static KafkaTemplate<String, String> getKafkaTemplate() {
        KafkaProperties.Producer producer = new KafkaProperties.Producer();
        producer.setBootstrapServers(Collections.singletonList("203.156.205.102:9092"));
        producer.setClientId("test-prod-client-id");
        DefaultKafkaProducerFactory<String, String> kafkaProducerFactory = new DefaultKafkaProducerFactory<>(producer.buildProperties());
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    public static KafkaConsumer<String, String> getKafkaListener() {
        KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();
//        consumer.setBootstrapServers(Collections.singletonList("dev.kafka.deepq:11692"));
//        consumer.setBootstrapServers(Collections.singletonList("203.156.205.102:9092"));
        consumer.setBootstrapServers(Collections.singletonList("127.0.0.1:9092"));
//        consumer.setBootstrapServers(Arrays.asList("47.96.26.149:9092", "47.96.27.99:9092", "47.96.3.207:9092"));
        consumer.setGroupId("test_test");
        consumer.setAutoOffsetReset("latest");
        consumer.setMaxPollRecords(100);
        consumer.setEnableAutoCommit(false);
        consumer.setAutoCommitInterval(Duration.ofSeconds(15));
//        consumer.getProperties().put("security.protocol", "PLAINTEXT");
//        consumer.getProperties().put("sasl.mechanism", "PLAIN");
        return new KafkaConsumer<>(consumer.buildProperties());
    }
}
