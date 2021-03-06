package com.lhk.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
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

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    private static AtomicBoolean flag = new AtomicBoolean(true);

    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", "D:\\data-source\\客户数据\\深擎\\prod\\kafka_client_jaas.conf");
        readKafkaMsg();
    }

    public static void readAndSendMsg() {
        System.setProperty("java.security.auth.login.config", "D:\\data-source\\deepq\\prod\\kafka_client_jaas.conf");
        KafkaConsumer<String, String> kafkaListener = getKafkaListener();
        kafkaListener.subscribe(Collections.singletonList(""));
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
        kafkaListener.subscribe(Collections.singletonList("origin-owl"));
        while (flag.get()) {
            long startTime = System.currentTimeMillis();
            if (executor.getActiveCount() < executor.getCorePoolSize() - 1) {
                ConsumerRecords<String, String> consumerRecords = kafkaListener.poll(Duration.of(1, ChronoUnit.SECONDS));
                System.out.println("fetch time is " + (System.currentTimeMillis() - startTime));
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    System.out.println(record.topic());
                    executor.execute(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(record.value());
                    });
                }
                try {
                    if (!consumerRecords.isEmpty()) {
                        kafkaListener.commitSync();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
//        consumer.setBootstrapServers(Collections.singletonList("127.0.0.1:9092"));
        consumer.setBootstrapServers(Arrays.asList("47.96.26.149:9092", "47.96.27.99:9092", "47.96.3.207:9092"));
//        consumer.setBootstrapServers(Collections.singletonList("47.103.102.172:9092"));
        consumer.setGroupId("origin-owl_red_bank_prod");
        consumer.setAutoOffsetReset("latest");
        consumer.setMaxPollRecords(100);
        consumer.getProperties().put("session.timeout.ms", "15000");
        consumer.setEnableAutoCommit(false);
//        consumer.setAutoCommitInterval(Duration.ofSeconds(15));
//        consumer.getProperties().put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"alikafka_pre-cn-v0h11864z005\" password=\"spCRBSdNPU7cqDyxYtTekqq4drNgROvx\";");
        consumer.getProperties().put("security.protocol", "SASL_PLAINTEXT");
        consumer.getProperties().put("sasl.mechanism", "PLAIN");
        return new KafkaConsumer<>(consumer.buildProperties());
    }
}
