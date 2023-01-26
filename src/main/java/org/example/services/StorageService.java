package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.example.entities.AggregationData;
import org.example.entities.NodePayload;
import org.example.entities.Note;
import org.example.entities.Thresholds;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

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

            firebaseDatabase = FirebaseDatabase.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save new coming data to firebase RTDB
     * Does not contain CountDownLatch, to be used in SparkJob
     *
     * @param payloadDataset dataset of NodePayload to be saved in RTDB
     * */
    public void saveDatasetToRTDB(Dataset<NodePayload> payloadDataset){
        DatabaseReference productsRef = firebaseDatabase.getReference("products/");
        Map<String, Object> productUpdates = new HashMap<>();

        payloadDataset.collectAsList().forEach(nodePayload -> {
            String nodePath = nodePayload.getProductId()+"/nodes/"+nodePayload.getNodeId();
            productUpdates.put(nodePath, nodePayload);
        });

        productsRef.updateChildrenAsync(productUpdates);
    }

    /**
     * Save new coming data to firebase RTDB,
     * Function for testing, contain CountDownLatch
     *
     * @param payloadDataset dataset of NodePayload to be saved in RTDB
     * */
    public void saveDatasetToRTDBForTest(Dataset<NodePayload> payloadDataset){
        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference productsRef = firebaseDatabase.getReference("products/");
        Map<String, Object> productUpdates = new HashMap<>();

        payloadDataset.collectAsList().forEach(nodePayload -> {
            String nodePath = nodePayload.getProductId()+"/nodes/"+nodePayload.getNodeId();
            productUpdates.put(nodePath, nodePayload);
        });

        productsRef.updateChildren(productUpdates, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                System.out.println("Data could not be saved " + databaseError.getMessage());
            } else {
                System.out.println("Data saved successfully.");
            }
            done.countDown();
        });
        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up a firebase listener to update a product's aggregation data
     * whenever one of its nodes update its values.
     * The function must be called only once during the lifetime of the application
     * */
    public void setUpdateAggregationDataListener(){
        DatabaseReference productsRef = firebaseDatabase.getReference("products/");
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                AggregationData aggregationData = new AggregationData();
                dataSnapshot.child("nodes").getChildren().forEach(dataSnapshot1 -> {
                    NodePayload payload = dataSnapshot1.getValue(NodePayload.class);
                    aggregationData.sumNodeValues(payload.getValues());
                });
                aggregationData.divideByNodesNumber(dataSnapshot.child("nodes").getChildrenCount());
                updateAggregationDataInRTDB(dataSnapshot.getKey(), aggregationData);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Update aggregation data for a specific product
     *
     * @param productKey the product key to be updated
     * @param aggregationData the new data to be saved
     * */
    public void updateAggregationDataInRTDB(String productKey, AggregationData aggregationData){
        DatabaseReference productsRef = firebaseDatabase.getReference("products/"+productKey);
        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("aggregation", aggregationData);
        productsRef.updateChildrenAsync(productUpdates);
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
