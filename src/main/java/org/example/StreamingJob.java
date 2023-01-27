package org.example;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.example.entities.NodePayload;
import org.example.services.DataCleaningService;
import org.example.services.DataValidationService;
import org.example.services.StorageService;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StreamingJob {

    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        //init
        InputStream properties = StreamingJob.class.getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(properties);
        DataCleaningService dataCleaningService = new DataCleaningService();
        StorageService storageService = new StorageService();
        DataValidationService dataValidationService = new DataValidationService();

        // remove INFO logs
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        System.out.println("[Spark Streaming Job started]");
        SparkSession spark = SparkSession.builder().appName("spark-app").master("spark://spark-master:7077")
                .getOrCreate();

        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "demo").option("includeHeaders", "true").load();

        df = df.selectExpr("CAST(topic AS STRING)", "CAST(partition AS STRING)", "CAST(offset AS STRING)",
                "CAST(value AS STRING)");

        StructType schema = getPayloadSchema();

        df = df.select(functions.col("topic"), functions.col("partition")
                , functions.col("offset")
                , functions.from_json(functions.col("value"), schema).alias("data")
        );
        df = df.select("topic", "partition", "offset", "data.*");

        Dataset<Row> responseWithSelectedColumns = df.select(functions.col("values"),
                functions.col("timestamp"), functions.col("nodeId"), functions.col("productId")
                , functions.col("values").getField("tempSoil")
                , functions.col("values").getField("tempAir")
                , functions.col("values").getField("humidity")
                , functions.col("values").getField("moisture")
                , functions.col("values").getField("ph")
                , functions.col("values").getField("npk").getField("n")
                , functions.col("values").getField("npk").getField("p")
                , functions.col("values").getField("npk").getField("k")
        );

        Dataset<NodePayload> nodePayloadDatasetDataset = responseWithSelectedColumns
                .as(Encoders.bean(NodePayload.class));

        // Set UpdateAggregationDataListener only once
        storageService.setUpdateAggregationDataListener();

        StreamingQuery query = nodePayloadDatasetDataset.writeStream()
                .foreachBatch(
                        (VoidFunction2<Dataset<NodePayload>, Long>) (dataset, batchId) -> {
                            System.out.println("\n[New Data - Batch: "+batchId+"]\n");
                            // Processing
                            dataset = dataCleaningService.removeMissingValues(dataset);
                            dataset = dataCleaningService.removeNoiseAndIncorrectData(dataset);
                            dataset.show(); // show dataset after cleaning
                            dataValidationService.validatePayload(dataset, storageService);
                            storageService.saveDatasetToRTDB(dataset);
                        }
                )
                .start();

        query.awaitTermination();
    }

    public static StructType getPayloadSchema(){
        List<StructField> fieldsOfNpkField = new ArrayList<>();
        fieldsOfNpkField.add(new StructField("n", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfNpkField.add(new StructField("p", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfNpkField.add(new StructField("k", DataTypes.DoubleType, true, Metadata.empty()));

        List<StructField> fieldsOfValuesField = new ArrayList<>();
        fieldsOfValuesField.add(new StructField("tempSoil", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfValuesField.add(new StructField("tempAir", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfValuesField.add(new StructField("humidity", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfValuesField.add(new StructField("moisture", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfValuesField.add(new StructField("ph", DataTypes.DoubleType, true, Metadata.empty()));
        fieldsOfValuesField.add(new StructField("npk", DataTypes.createStructType(fieldsOfNpkField), true, Metadata.empty()));

        return new StructType(new StructField[]{
                new StructField("values", DataTypes.createStructType(fieldsOfValuesField), true, Metadata.empty()),
                new StructField("timestamp", DataTypes.TimestampType, true, Metadata.empty()),
                new StructField("nodeId", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("productId", DataTypes.IntegerType, true, Metadata.empty()),
        });
    }

}