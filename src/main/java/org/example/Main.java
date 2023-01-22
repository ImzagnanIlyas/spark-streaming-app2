package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import kafka.serializer.StringDecoder;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.kafka.common.serialization.StringDeserializer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // remove INFO logs
//        Logger.getLogger("org").setLevel(Level.ERROR);
//        Logger.getLogger("akka").setLevel(Level.ERROR);

        System.out.println("Hello world!");
        System.out.println(System.getProperty("java.version"));

        System.out.println("Spark Streaming started now .....");

//        SparkConf conf = new SparkConf().setAppName("spark-app").setMaster("local[*]");
        SparkConf conf = new SparkConf().setAppName("spark-app").setMaster("spark://spark-master:7077");
        JavaSparkContext sc = new JavaSparkContext(conf);
        // batchDuration - The time interval at which streaming data will be divided into batches
        JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(10000));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafka:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "group_id");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = List.of("demo");

//        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
//                String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
        JavaInputDStream<ConsumerRecord<String, String>> directKafkaStream = KafkaUtils.createDirectStream(
                ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams)
        );
//        JavaDStream<String> lines = ssc.textFileStream("C:\\Users\\HP\\Downloads\\Spark_Streams");


        List<String> allRecord = new ArrayList<String>();
        final String COMMA = ",";

        directKafkaStream.foreachRDD(rdd -> {
            System.out.println("New data arrived  " + rdd.partitions().size() +" Partitions and " + rdd.count() + " Records");
            rdd.collect().forEach(System.out::println);
        });

        ssc.start();
        ssc.awaitTermination();
    }
}