package com.gestormascotas.firebase;

import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FirebaseService {
    private final DatabaseReference db;

    public FirebaseService() {
        this.db = FirebaseDatabase.getInstance().getReference();
    }

    // Generar ID secuencial
    public String generarIdSecuencial(String tipo) throws Exception {
        DatabaseReference refContador = db.child("contadores").child(tipo);
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        refContador.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        DataSnapshot snapshot = future.get();
        long contadorActual = 0;
        if (snapshot.exists()) {
            Object val = snapshot.getValue();
            if (val instanceof Long) contadorActual = (Long) val;
            else if (val instanceof Integer) contadorActual = ((Integer) val).longValue();
            else if (val != null) contadorActual = Long.parseLong(val.toString());
        }
        long nuevoValor = contadorActual + 1;
        refContador.setValueAsync(nuevoValor);
        return tipo + "_" + String.format("%04d", nuevoValor);
    }

    // Listar todos los nodos (por ejemplo "duenos", "mascotas", "citas")
    public List<DataSnapshot> listarTodos(String rama) throws Exception {
        DatabaseReference ref = db.child(rama);
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        DataSnapshot snapshot = future.get();
        List<DataSnapshot> lista = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                lista.add(child);
            }
        }
        return lista;
    }

    // Buscar por nombre
    public List<DataSnapshot> buscarPorNombre(String rama, String nombre) throws Exception {
        DatabaseReference ref = db.child(rama);
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        ref.orderByChild("nombre").equalTo(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        DataSnapshot snapshot = future.get();
        List<DataSnapshot> lista = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                lista.add(child);
            }
        }
        return lista;
    }

    public DatabaseReference getDb() {
        return db;
    }
}
