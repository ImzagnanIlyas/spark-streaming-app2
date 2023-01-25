package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.example.entities.Note;
import org.example.entities.Thresholds;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class StorageService {
    private static final String DATABASE_URL = "https://internet-of-tomato-farming-default-rtdb.firebaseio.com/";
    private FirebaseDatabase firebaseDatabase;
    private static Firestore firestore;
    private static FirebaseApp firebaseApp;
    private static FirebaseMessaging firebaseMessaging;


    public StorageService() {
        initializeFirebase();
    }

    void initializeFirebase(){
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/service-account-file.json");
            FileInputStream serviceAccount2 = new FileInputStream("src/main/resources/service-account-file.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);

            FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount2))
                    .setProjectId("internet-of-tomato-farming")
                    .build();
            firestore = firestoreOptions.getService();

            firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save new coming data to firebase
     *
     * @param nodes new rows to be saved
     * */
    public <T> void savePayloadsToRTDB(Map<String, T> nodes){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference nodesRef = database.getReference("products/123456789/nodes");

        CountDownLatch done = new CountDownLatch(1);
//        nodesRef.setValueAsync(nodes);
        nodesRef.setValue(nodes,  new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
                done.countDown();
            }
        });
        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Thresholds readThresholdsFromDB(String productId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection("usersData").document(productId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot documentSnapshot = future.get();
        Thresholds thresholds = null;
        if(documentSnapshot.exists()) {
            thresholds = documentSnapshot.toObject(Thresholds.class);
            System.out.println(thresholds);
            System.out.println("Document data: " + documentSnapshot.getData());
        }else {
            System.out.println("No such doc");
        }
        return thresholds;
    }


    public static String sendNotification(Note note, String topic) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getData().toString())
                .build();

        Message message = Message
                .builder()
                .setTopic(topic)
                .setNotification(notification)
                .build();

        return firebaseMessaging.send(message);
    }

}
