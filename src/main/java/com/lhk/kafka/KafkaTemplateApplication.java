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
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaTemplateApplication {

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
        kafkaListener.subscribe(Collections.singletonList("article-with-fund-tags"));
        while (flag.get()) {
            ConsumerRecords<String, String> consumerRecords = kafkaListener.poll(Duration.of(1, ChronoUnit.SECONDS));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                System.out.println(record.value());
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
//        consumer.setBootstrapServers(Collections.singletonList("203.156.205.102:9092"));
        consumer.setBootstrapServers(Arrays.asList("47.96.26.149:9092", "47.96.27.99:9092", "47.96.3.207:9092"));
        consumer.setGroupId("article-with-fund-tags-test");
        consumer.setAutoOffsetReset("earliest");
        consumer.setEnableAutoCommit(true);
        consumer.setAutoCommitInterval(Duration.ofSeconds(15));
        consumer.getProperties().put("security.protocol", "SASL_PLAINTEXT");
        consumer.getProperties().put("sasl.mechanism", "PLAIN");
        return new KafkaConsumer<>(consumer.buildProperties());
    }
}
