import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.BasicConfigurator;
import org.example.entities.Note;
import org.example.services.DataValidationService;
import org.example.services.StorageService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainForTest {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        BasicConfigurator.configure();
        StorageService storageService = new StorageService();
        storageService.readThresholdsFromDB("eB82wllWkZpAW5kmKfCw");

        DataValidationService dataValidationService = new DataValidationService();
        dataValidationService.triggerAlert(DataValidationService.TypeOfAlert.MAX, DataValidationService.Sensor.Moisture, "1234", "3333", "23");
        dataValidationService.triggerAlert(DataValidationService.TypeOfAlert.MAX, DataValidationService.Sensor.Moisture, "1234", "3333", "23");
        dataValidationService.triggerAlert(DataValidationService.TypeOfAlert.MAX, DataValidationService.Sensor.TempAir, "1234", "3333", "23");

/*        Note note = new Note();
        note.setSubject("some subject");
        Map<String, String> data = new HashedMap();
        data.put("TypeOfAlert","MIN");
        data.put("Sensor","Humidity");
        data.put("NodeID","Value 1");
        data.put("ProductID","Value 1");
        data.put("DateTime","22/11/2023");
        data.put("Value","1251");

        note.setData(data);
        try {
            System.out.println("response : "+ StorageService.sendNotification(note, "demo"));
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
        }*/


    }
}
