package org.example.services;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.example.entities.NodePayload;
import org.example.entities.NpkPayload;

public class AggregationService {
    private static double averageTempSoil = 0;
    private static double averageTempAir = 0;
    private static double averageHumidity = 0;
    private static double averageMoisture = 0;
    private static double averagePh = 0;
    private static double averageN = 0;
    private static double averageP = 0;
    private static double averageK = 0;

    public void calculateAverage(Dataset<NodePayload> nodePayloadDatasetDataset){
        nodePayloadDatasetDataset.foreach(nodePayload -> {
            averageTempSoil+=nodePayload.getValues().getTempSoil();
            averageTempAir+=nodePayload.getValues().getTempAir();
            averageHumidity+=nodePayload.getValues().getHumidity();
            averageMoisture+=nodePayload.getValues().getMoisture();
            averagePh+=nodePayload.getValues().getPh();
            averageN+=nodePayload.getValues().getNpk().getN();
            averageP+=nodePayload.getValues().getNpk().getP();
            averageK+=nodePayload.getValues().getNpk().getK();
        });

    }

    //TODO
    public static void storeAggregationValues(){

    }

    public static double getAverageTempSoil() {
        return averageTempSoil;
    }

    public static void setAverageTempSoil(double averageTempSoil) {
        AggregationService.averageTempSoil = averageTempSoil;
    }

    public static double getAverageTempAir() {
        return averageTempAir;
    }

    public static void setAverageTempAir(double averageTempAir) {
        AggregationService.averageTempAir = averageTempAir;
    }

    public static double getAverageHumidity() {
        return averageHumidity;
    }

    public static void setAverageHumidity(double averageHumidity) {
        AggregationService.averageHumidity = averageHumidity;
    }

    public static double getAverageMoisture() {
        return averageMoisture;
    }

    public static void setAverageMoisture(double averageMoisture) {
        AggregationService.averageMoisture = averageMoisture;
    }

    public static double getAveragePh() {
        return averagePh;
    }

    public static void setAveragePh(double averagePh) {
        AggregationService.averagePh = averagePh;
    }

    public static double getAverageN() {
        return averageN;
    }

    public static void setAverageN(double averageN) {
        AggregationService.averageN = averageN;
    }

    public static double getAverageP() {
        return averageP;
    }

    public static void setAverageP(double averageP) {
        AggregationService.averageP = averageP;
    }

    public static double getAverageK() {
        return averageK;
    }

    public static void setAverageK(double averageK) {
        AggregationService.averageK = averageK;
    }
}
