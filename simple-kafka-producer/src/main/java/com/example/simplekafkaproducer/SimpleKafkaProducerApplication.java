package com.example.simplekafkaproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

//@SpringBootApplication
@Slf4j
public class SimpleKafkaProducerApplication {
	private final static String TOPIC_NAME = "test";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092";

	public static void main(String[] args) {
		Properties configs = new Properties();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

		String messageValue = "testMessage";
		ProducerRecord<String, String> recode = new ProducerRecord<>(TOPIC_NAME, messageValue);
		ProducerRecord<String, String> recode2 = new ProducerRecord<>("test", "keyTest","123");

		producer.send(recode);
		producer.send(recode2);
		log.info("recode ======> {}", recode);
		producer.flush();
		producer.close();


//		SpringApplication.run(SimpleKafkaProducerApplication.class, args);




	}

}
