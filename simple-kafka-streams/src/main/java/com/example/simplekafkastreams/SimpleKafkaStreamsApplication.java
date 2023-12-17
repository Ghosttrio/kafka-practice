package com.example.simplekafkastreams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Properties;

//@SpringBootApplication
@Slf4j
public class SimpleKafkaStreamsApplication {

	private static String APPLICATION_NAME = "streams-application";
	private static String BOOTSTRAP_SERVERS = "localhost:9092";
	private static String STREAM_LOG = "stream_log";
	private static String STREAM_LOG_COPY = "stream_log_copy";

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> streamLog = builder.stream(STREAM_LOG);
		streamLog.to(STREAM_LOG_COPY);

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();


//		SpringApplication.run(SimpleKafkaStreamsApplication.class, args);
	}

}
