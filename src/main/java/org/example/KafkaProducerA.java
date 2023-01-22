package org.example;

import java.util.Properties;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerA {
    private static String KafkaBrokerEndpoint = "localhost:9095";
    private static String KafkaTopic = "demo";

    private Producer<String, String> ProducerProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBrokerEndpoint);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaCsvProducer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<String, String>(properties);
    }

    public static void main(String[] args) throws URISyntaxException {
        KafkaProducerA kafkaProducer = new KafkaProducerA();
        kafkaProducer.publishMessages();
        System.out.println("Producing job completed");
    }

    private void publishMessages() throws URISyntaxException{

        final Producer<String, String> stringProducer = ProducerProperties();

        for (int i = 0; i < 30; i++) {
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                    KafkaTopic, UUID.randomUUID().toString(), "line"+i);

            stringProducer.send(record, (metadata, exception) -> {
                if(metadata != null){
                    System.out.println("Data: -> "+ record.key()+" | "+ record.value());
                }
                else{
                    System.out.println("Error Sending Record -> "+ record.value());
                }
            });
        }
    }
}
