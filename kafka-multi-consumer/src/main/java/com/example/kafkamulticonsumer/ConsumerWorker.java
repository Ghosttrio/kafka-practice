package com.example.kafkamulticonsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConsumerWorker implements Runnable{
    private static Map<Integer, List<String>> bufferString = new ConcurrentHashMap<>();
    private static Map<Integer, Long> currentFileOffset = new ConcurrentHashMap<>();
    private Properties prop;
    private String topic;
    private String threadName;
    private KafkaConsumer<String, String> consumer;

    public ConsumerWorker(Properties prop, String topic, String threadName, int number) {
        log.info("Generate ConsumerWorker");
        this.prop = prop;
        this.topic = topic;
        this.threadName = "consumer-thread-" + number;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);

        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(Arrays.asList(topic));

        try {
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for(ConsumerRecord<String, String> record : records){
                    addHdfsFileBuffer(record);
                }
                saveBufferToHdfsFile(consumer.assignment());

            }
        }catch (WakeupException e){
            log.warn("Wakeup consumer");
        }catch (Exception e){
            log.info(e.getMessage(), e);
        }finally {
            consumer.close();
        }

    }

    private void addHdfsFileBuffer(ConsumerRecord<String, String> record){
        List<String> buffer = bufferString.getOrDefault(record.partition(), new ArrayList<>());
        buffer.add(record.value());
        bufferString.put(record.partition(), buffer);
        if(buffer.size() == 1){
            currentFileOffset.put(record.partition(), record.offset());
        }
    }

    private void saveBufferToHdfsFile(Set<TopicPartition> partitions){
        partitions.forEach(p -> checkFlushCount(p.partition()));
    }

    private void checkFlushCount(int partitionNo){
        if(bufferString.getOrDefault(partitionNo) != null){
            if (bufferString.get(partitionNo).size() > 10 - 1){
                save(partitionNo);
            }
        }
    }

    private void save(int partitionNo){
        if (bufferString.get(partitionNo).size() > 0){
            try {
                String fileName = "/data/color-" + partitionNo + "-" + currentFileOffset.get(partitionNo)  + ".log";
                Configuration configuration = new Configuration();
                configuration.set("fs.defaultFS", "hdfs://localhost:9000");
                FileSystem.get(configuration);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }
}
