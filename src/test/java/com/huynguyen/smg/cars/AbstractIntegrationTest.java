package com.huynguyen.smg.cars;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


public class AbstractIntegrationTest {

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
        DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.14.0"));

    @DynamicPropertySource
    static void elasticSearchProperties(DynamicPropertyRegistry registry) {
        elasticsearchContainer.start();
        registry.add("spring.elasticsearch.uris", () -> "http://0.0.0.0:" + elasticsearchContainer.getMappedPort(9200));
        registry.add("event.listeners.car.topic", () -> "test-car-topic");

    }

    @DynamicPropertySource
    static void kafkaProperty(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
