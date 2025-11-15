package com.gestormascotas.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInit {
    public static void init() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/proyecto-programacion-2-530e5-firebase-adminsdk-fbsvc-5181f6245d.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://proyecto-programacion-2-530e5-default-rtdb.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al inicializar Firebase: " + e.getMessage());
        }
    }
}
