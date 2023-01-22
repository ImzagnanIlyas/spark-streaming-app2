package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.functions;

import java.util.concurrent.TimeoutException;

public class StreamingJob {

    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        // remove INFO logs
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        System.out.println("Hello world!");
//        SparkSession spark = SparkSession.builder().appName("MyStructuredStreamingJob").master("spark://spark-master:7077")
//                .getOrCreate();
        SparkSession spark = SparkSession.builder().appName("MyStructuredStreamingJob").master("local[*]")
                .getOrCreate();

//        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "kafka:9092")
//                .option("subscribe", "demo").option("includeHeaders", "true").load();
        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "localhost:9095")
                .option("subscribe", "demo").option("includeHeaders", "true").load();

        df = df.selectExpr("CAST(topic AS STRING)", "CAST(partition AS STRING)", "CAST(offset AS STRING)", "CAST(value AS STRING)");

        StructType emp_schema = new StructType().add("name", DataTypes.StringType).add("age", DataTypes.StringType)
                .add("country", DataTypes.StringType);

        df = df.select(functions.col("topic"), functions.col("partition"), functions.col("offset"), functions.from_json(functions.col("value"), emp_schema).alias("data"));
        df = df.select("topic", "partition", "offset", "data.*");

        StreamingQuery query = df.writeStream().format("console").option("truncate", "False").start();
        query.awaitTermination();
    }
}
