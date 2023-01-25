package org.example.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.type.DateTime;
import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.example.entities.NodePayload;
import org.example.entities.Note;
import org.example.entities.Thresholds;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DataValidationService {
    FirebaseMessagingService firebaseMessagingService;
    Map<Pair, Date> todaysNotifs;

    private enum TypeOfAlert{
        MIN,
        MAX
    }
    private enum Sensor{
        TempSoil,
        TempAir,
        Humidity,
        Moisture,
        Ph,
        N,
        P,
        K
    }

    public DataValidationService() throws IOException {
        this.firebaseMessagingService = new FirebaseMessagingService();
        this.todaysNotifs = new HashedMap();
    }

    public void triggerAlert(TypeOfAlert typeOfAlert, Sensor sensor, String nodeID, String productId, String value){
        Note note = new Note();
        note.setSubject("some subject");
        Map<String, String> data = new HashedMap();
        data.put("TypeOfAlert",typeOfAlert.name());
        data.put("Sensor",sensor.name());
        data.put("NodeID",nodeID);
        data.put("ProductID",productId);
        data.put("Value",value);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.put("DateTime", formatter.format(date));


        note.setData(data);
        try {
            //TODO check before send
            System.out.println("response : "+ firebaseMessagingService.sendNotification(note, "demo"));
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void validatePayload(Dataset<NodePayload> nodePayloadDatasetDataset, Thresholds thresholds){
        nodePayloadDatasetDataset.foreach((ForeachFunction<NodePayload>) nodePayload -> {
            if(nodePayload.getValues().getHumidity() < thresholds.getThresholdHumidityMin()) triggerAlert(TypeOfAlert.MIN,
                    Sensor.Humidity ,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getHumidity()));
            if(nodePayload.getValues().getHumidity() > thresholds.getThresholdHumidityMax()) triggerAlert(TypeOfAlert.MAX, Sensor.Humidity,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getHumidity()));

            if(nodePayload.getValues().getMoisture() < thresholds.getThresholdMoistureMin()) triggerAlert(TypeOfAlert.MIN, Sensor.Moisture,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getMoisture()));
            if(nodePayload.getValues().getMoisture() > thresholds.getThresholdMoistureMax()) triggerAlert(TypeOfAlert.MAX, Sensor.Moisture,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getMoisture()));

            if(nodePayload.getValues().getTempSoil() < thresholds.getThresholdTempSoilMin()) triggerAlert(TypeOfAlert.MIN, Sensor.TempSoil,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getTempSoil()));
            if(nodePayload.getValues().getTempSoil() > thresholds.getThresholdTempSoilMax()) triggerAlert(TypeOfAlert.MAX, Sensor.TempSoil,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getTempSoil()));

            if(nodePayload.getValues().getTempAir() < thresholds.getThresholdTempAirMin()) triggerAlert(TypeOfAlert.MIN, Sensor.TempAir,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getTempAir()));
            if(nodePayload.getValues().getTempAir() > thresholds.getThresholdTempAirMax()) triggerAlert(TypeOfAlert.MAX, Sensor.TempAir,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getTempAir()));

            if(nodePayload.getValues().getPh() < thresholds.getThresholdPhMin()) triggerAlert(TypeOfAlert.MIN, Sensor.Ph,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getPh()));
            if(nodePayload.getValues().getPh() > thresholds.getThresholdPhMax()) triggerAlert(TypeOfAlert.MAX, Sensor.Ph,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getPh()));

            if(nodePayload.getValues().getNpk().getN() < thresholds.getThresholdNMin()) triggerAlert(TypeOfAlert.MIN, Sensor.N,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getN()));
            if(nodePayload.getValues().getNpk().getN() > thresholds.getThresholdNMax()) triggerAlert(TypeOfAlert.MAX, Sensor.N ,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getN()));

            if(nodePayload.getValues().getNpk().getP() < thresholds.getThresholdPMin()) triggerAlert(TypeOfAlert.MIN, Sensor.P,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getP()));
            if(nodePayload.getValues().getNpk().getP() > thresholds.getThresholdPMax()) triggerAlert(TypeOfAlert.MAX, Sensor.P,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getP()));

            if(nodePayload.getValues().getNpk().getK() < thresholds.getThresholdKMin()) triggerAlert(TypeOfAlert.MIN, Sensor.K,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getK()));
            if(nodePayload.getValues().getNpk().getK() > thresholds.getThresholdKMax()) triggerAlert(TypeOfAlert.MAX, Sensor.K,String.valueOf(nodePayload.getNodeId()) , String.valueOf(nodePayload.getProductId()), String.valueOf(nodePayload.getValues().getNpk().getK()));
            
        });
    }
    
    public boolean sameDay(Date date1, Date date2){

        return false;
    }

    public class Pair{
        public String nodeId;
        public String productId;

        public Pair(String nodeId, String productId) {
            this.nodeId = nodeId;
            this.productId = productId;
        }

        public Pair() {
        }

    }

}


