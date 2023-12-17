package com.example.simplekafkastreamsjoin;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

//@SpringBootApplication
@Slf4j
public class SimpleKafkaStreamsJoinApplication {

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-join-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder builder = new StreamsBuilder();
		KTable<String, String> addressTable = builder.table("address");
		KStream<String, String> orderStream = builder.stream("order");

		orderStream.join(addressTable,
				(order, address) -> order + " send to " + address)
				.to("order_join");

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();

//		SpringApplication.run(SimpleKafkaStreamsJoinApplication.class, args);
	}

}
