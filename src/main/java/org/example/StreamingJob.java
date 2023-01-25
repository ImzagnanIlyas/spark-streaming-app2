package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.example.entities.NodePayload;
import org.example.entities.SensorValues;
import org.example.services.DataCleaningService;
import scala.Function1;
import scala.collection.JavaConversions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StreamingJob {

    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        //init
        DataCleaningService dataCleaningService = new DataCleaningService();

        // remove INFO logs
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        System.out.println("Hello world!");
        SparkSession spark = SparkSession.builder().appName("spark-app").master("spark://spark-master:7077")
                .getOrCreate();
//        SparkSession spark = SparkSession.builder().appName("MyStructuredStreamingJob").master("local[*]")
//                .getOrCreate();

        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "demo").option("includeHeaders", "true").load();
//        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "localhost:9095")
//                .option("subscribe", "demo").option("includeHeaders", "true").load();

        df = df.selectExpr("CAST(topic AS STRING)", "CAST(partition AS STRING)", "CAST(offset AS STRING)", "CAST(value AS STRING)");

//        StructType emp_schema = new StructType().add("name", DataTypes.StringType).add("age", DataTypes.StringType)
//                .add("country", DataTypes.StringType);

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

        StructType schema = new StructType(new StructField[]{
                new StructField("values", DataTypes.createStructType(fieldsOfValuesField), true, Metadata.empty()),
                new StructField("timestamp", DataTypes.TimestampType, true, Metadata.empty()),
                new StructField("nodeId", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("productId", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("grid", DataTypes.StringType, true, Metadata.empty()),
        });

        df = df.select(functions.col("topic"), functions.col("partition"), functions.col("offset"), functions.from_json(functions.col("value"), schema).alias("data"));
        //df = df.select("data.*");
        df = df.select("topic", "partition", "offset", "data.*");

        Dataset<Row> responseWithSelectedColumns = df.select(functions.col("values"),
                functions.col("timestamp"), functions.col("nodeId"), functions.col("productId"),functions.col("grid"), functions.col("values").getField("tempSoil")
                , functions.col("values").getField("tempAir") , functions.col("values").getField("humidity")
                , functions.col("values").getField("moisture") , functions.col("values").getField("ph")
                , functions.col("values").getField("npk").getField("n"), functions.col("values").getField("npk").getField("p")
                , functions.col("values").getField("npk").getField("k"));

        Dataset<NodePayload> nodePayloadDatasetDataset = responseWithSelectedColumns
                .as(Encoders.bean(NodePayload.class));

        nodePayloadDatasetDataset = dataCleaningService.removeMissingValues(nodePayloadDatasetDataset);

        StreamingQuery query = df.writeStream().format("console").option("truncate", "False").start();
        StreamingQuery query1 = nodePayloadDatasetDataset.writeStream()
                .format("console")
                .start();


        query.awaitTermination();
        query1.awaitTermination();
    }
}