import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.BasicConfigurator;
import org.example.entities.Note;
import org.example.services.FirebaseMessagingService;
import org.example.services.StorageServiceTobeDeleted;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainForTest {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        BasicConfigurator.configure();
/*        StorageServiceTobeDeleted storageServiceTobeDeleted = new StorageServiceTobeDeleted();
        storageServiceTobeDeleted.initFirestore();
        storageServiceTobeDeleted.readThresholdsFromDB("eB82wllWkZpAW5kmKfCw");*/


        FirebaseMessagingService firebaseMessagingService = new FirebaseMessagingService();
        Note note = new Note();
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
            System.out.println("response : "+ firebaseMessagingService.sendNotification(note, "demo"));
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
        }


    }
}
