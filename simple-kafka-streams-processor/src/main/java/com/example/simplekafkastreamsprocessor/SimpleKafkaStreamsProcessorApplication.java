package com.example.simplekafkastreamsprocessor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

//@SpringBootApplication
public class SimpleKafkaStreamsProcessorApplication {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "processor-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		Topology topology = new Topology();
		topology.addSource("Source", "stream_log")
				.addProcessor("Process", () -> new FilterProcessor(),
						"Source")
				.addSink("Sink", "stream_log_filter", "Process");

		new KafkaStreams(topology, props).start();


//		SpringApplication.run(SimpleKafkaStreamsProcessorApplication.class, args);
	}

}
