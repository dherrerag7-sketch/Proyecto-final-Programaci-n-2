package com.gestormascotas;

import com.gestormascotas.firebase.FirebaseInit;
import com.gestormascotas.firebase.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        FirebaseInit.init();
        FirebaseService service = new FirebaseService();
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("--- Gestor de Mascotas ---");
            System.out.println("1. Registrar Dueño");
            System.out.println("2. Registrar Mascota");
            System.out.println("3. Registrar Cita");
            System.out.println("4. Listar Dueños");
            System.out.println("5. Listar Mascotas");
            System.out.println("6. Listar Citas");
            System.out.println("7. Buscar Dueño por nombre");
            System.out.println("8. Buscar Mascota por nombre");
            System.out.println("9. Salir");
            System.out.print("Elige una opción: ");
            String opt = sc.nextLine();

            switch (opt) {
                case "1" -> registrarDueno(service, sc);
                case "2" -> registrarMascota(service, sc);
                case "3" -> registrarCita(service, sc);
                case "4" -> listar(service, "duenos");
                case "5" -> listar(service, "mascotas");
                case "6" -> listar(service, "citas");
                case "7" -> buscar(service, "duenos", sc);
                case "8" -> buscar(service, "mascotas", sc);
                case "9" -> salir = true;
                default -> System.out.println("Opción inválida");
            }
            System.out.println();
        }
        sc.close();
    }

    private static void registrarDueno(FirebaseService service, Scanner sc) throws Exception {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine();
        System.out.print("Dirección: ");
        String direccion = sc.nextLine();

        String id = service.generarIdSecuencial("dueno");
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("telefono", telefono);
        data.put("direccion", direccion);

        DatabaseReference ref = service.getDb().child("duenos").child(id);
        ref.setValueAsync(data);
        System.out.println("Dueño creado con ID: " + id);
    }

    private static void registrarMascota(FirebaseService service, Scanner sc) throws Exception {
        System.out.print("Nombre mascota: ");
        String nombre = sc.nextLine();
        System.out.print("Especie: ");
        String especie = sc.nextLine();
        System.out.print("Edad: ");
        int edad = Integer.parseInt(sc.nextLine());
        System.out.print("ID dueño (ej. dueno_0001): ");
        String duenoId = sc.nextLine();

        String id = service.generarIdSecuencial("mascota");
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("especie", especie);
        data.put("edad", edad);
        data.put("duenoId", duenoId);

        DatabaseReference ref = service.getDb().child("mascotas").child(id);
        ref.setValueAsync(data);
        System.out.println("Mascota creada con ID: " + id);
    }

    private static void registrarCita(FirebaseService service, Scanner sc) throws Exception {
        System.out.print("ID mascota (ej. mascota_0001): ");
        String mascotaId = sc.nextLine();
        System.out.print("Fecha (dd-MM-yyyy): ");
        String fechaStr = sc.nextLine();
        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd-MM-yyyy").parse(fechaStr);
        } catch (ParseException e) {
            System.out.println("Formato de fecha inválido. Use dd-MM-yyyy");
            return;
        }
        System.out.print("Motivo: ");
        String motivo = sc.nextLine();
        System.out.print("Veterinario: ");
        String vet = sc.nextLine();

        String id = service.generarIdSecuencial("cita");
        Map<String, Object> data = new HashMap<>();
        data.put("mascotaId", mascotaId);
        data.put("fecha", new SimpleDateFormat("dd-MM-yyyy").format(fecha));
        data.put("motivo", motivo);
        data.put("veterinario", vet);

        DatabaseReference ref = service.getDb().child("citas").child(id);
        ref.setValueAsync(data);
        System.out.println("Cita creada con ID: " + id);
    }

    private static void listar(FirebaseService service, String rama) throws Exception {
        List<DataSnapshot> lista = service.listarTodos(rama);
        if (lista.isEmpty()) {
            System.out.println("No hay registros en " + rama);
            return;
        }
        System.out.println("Registros en " + rama + ":");
        for (DataSnapshot ds : lista) {
            String id = ds.getKey();
            Map map = (Map) ds.getValue();
            if (map == null) continue;
            if (rama.equals("citas")) {
                System.out.println(id + " - " + map.get("fecha") + " - " + map.get("motivo") + " - " + map.get("veterinario"));
            } else if (rama.equals("mascotas")) {
                System.out.println(id + " - " + map.get("nombre") + " (" + map.get("especie") + ") ID del dueño =" + map.get("duenoId"));
            } else {
                System.out.println(id + " - " + map.get("nombre") + " (" + map.get("telefono") + ")");
            }
        }
    }

    private static void buscar(FirebaseService service, String rama, Scanner sc) throws Exception {
        System.out.print("Nombre a buscar (coincidencia exacta): ");
        String nombre = sc.nextLine();
        List<DataSnapshot> resultados = service.buscarPorNombre(rama, nombre);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron registros con ese nombre.");
            return;
        }
        System.out.println("Resultados:");
        for (DataSnapshot ds : resultados) {
            String id = ds.getKey();
            Map map = (Map) ds.getValue();
            if (map == null) continue;
            if (rama.equals("mascotas")) {
                System.out.println(id + " - " + map.get("nombre") + " (" + map.get("especie") + ") ID del dueño=" + map.get("duenoId"));
            } else {
                System.out.println(id + " - " + map.get("nombre") + " (" + map.get("telefono") + ")");
            }
        }
    }
}
