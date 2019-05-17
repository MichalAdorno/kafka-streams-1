package com.myself.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class KSWordCounter {

    public static void main(String[] args) {

        final Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10*1000);
        final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, String> wordCountStreamBuilder = builder
                .stream("word-count-input");

        final KTable<String, Long> wordCountTable = wordCountStreamBuilder
                .mapValues(v -> v.toLowerCase())
                .flatMapValues(v -> Arrays.asList(v.split("\\W+")))
                .selectKey((k, v) -> v)
//                .groupBy((k, v) -> k)
                .groupByKey()
//                .reduce((s, v1) -> )
                .count(Materialized.as("Counts"));

        wordCountTable
                .toStream()
                .filter((k,v) -> v != null)
                .mapValues(v->v.toString())
                .to("word-count-output",
                        Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), properties);

        streams.cleanUp();
        streams.start();

        System.out.println(streams.toString());


        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
