package org.example.services;


import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.example.entities.Thresholds;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class StorageServiceTobeDeleted {

    private static Firestore firestore;

    public void initFirestore() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\oussa\\OneDrive\\Desktop\\PF\\spark-streaming-app2\\internet-of-tomato-farming-firebase-adminsdk-36wqo-78dae1c80a.json");

        FirestoreOptions options = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("internet-of-tomato-farming")
                .build();
        firestore = options.getService();

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

}
