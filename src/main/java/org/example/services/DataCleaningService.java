package org.example.services;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.example.entities.NodePayload;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class DataCleaningService {

    public Dataset<NodePayload> removeMissingValues(Dataset<NodePayload> nodePayloadDatasetDataset) {
        nodePayloadDatasetDataset = nodePayloadDatasetDataset.filter((FilterFunction<NodePayload>) nodePayload ->
        {
            if(nodePayload.getNodeId() == 0) return false;
            if(nodePayload.getProductId() == 0) return false;
            if(nodePayload.getTimestamp() == null) return false;
            if(nodePayload.getValues() == null) return false;

            if(nodePayload.getValues().getHumidity() == 0) return false;
            if(nodePayload.getValues().getMoisture() == 0) return false;
            if(nodePayload.getValues().getPh() == 0) return false;
            if(nodePayload.getValues().getTempSoil() == 0) return false;
            if(nodePayload.getValues().getTempAir() == 0) return false;
            if(nodePayload.getValues().getNpk() == null) return false;

            if(nodePayload.getValues().getNpk().getK() == 0) return false;
            if(nodePayload.getValues().getNpk().getN() == 0) return false;
            if(nodePayload.getValues().getNpk().getP() == 0) return false;
            return true;
        });
        return nodePayloadDatasetDataset;
    }

    public Dataset<NodePayload> removeNoiseAndIncorrectData(Dataset<NodePayload> nodePayloadDatasetDataset) {
        //Soil temperature sensor: -40°C to +85°C
        //Air temperature sensor: -40°C to +85°C
        //NPK sensor: Nitrogen is 0-10,000ppm, Phosphorus is 0-3,000ppm and Potassium is 0-10,000ppm
        //Humidity sensor:  0% to 100%
        //Moisture sensor: 0-100%
        // pH sensor: 0 and 14
        nodePayloadDatasetDataset = nodePayloadDatasetDataset.filter((FilterFunction<NodePayload>) nodePayload ->
        {
            if(nodePayload.getValues().getHumidity() < 100 && nodePayload.getValues().getHumidity() > 0) return false;
            if(nodePayload.getValues().getMoisture() < 100 && nodePayload.getValues().getMoisture() > 0) return false;
            if(nodePayload.getValues().getTempSoil() < 85 && nodePayload.getValues().getTempSoil() > -40) return false;
            if(nodePayload.getValues().getTempAir() < 85 && nodePayload.getValues().getTempAir() > -40) return false;
            if(nodePayload.getValues().getPh() < 14 && nodePayload.getValues().getPh() > 0) return false;

            if(nodePayload.getValues().getNpk().getN() < 10000 && nodePayload.getValues().getNpk().getN() > 0) return false;
            if(nodePayload.getValues().getNpk().getP() < 3000 && nodePayload.getValues().getNpk().getP() > 0) return false;
            if(nodePayload.getValues().getNpk().getK() < 10000 && nodePayload.getValues().getNpk().getK() > 0) return false;

            return true;
        });

        return nodePayloadDatasetDataset;
    }

}
