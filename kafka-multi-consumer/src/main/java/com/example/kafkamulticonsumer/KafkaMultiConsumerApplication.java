package com.example.kafkamulticonsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.codehaus.jackson.map.deser.std.StringDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KafkaMultiConsumerApplication {

	private final static List<ConsumerWorker> workers = new ArrayList<>();

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());


		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "color-hdfs-save-consumer-group");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		ExecutorService executorService = Executors.newCachedThreadPool();

		for (int i = 0; i < 3; i++){
			workers.add(new ConsumerWorker(config, "select-color", i));
		}
		workers.forEach(executorService::execute);
	}

	static class ShutdownThread extends Thread{
		public void run(){
			log.info("Shutdown hook");
			workers.forEach(ConsumerWorker::stopAndWakeup);
		}
	}

}
