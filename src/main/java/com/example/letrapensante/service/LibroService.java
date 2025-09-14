package com.example.letrapensante.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.letrapensante.Libro;

@Service
public class LibroService {
    // Ruta absoluta hacia src/main/resources/libros.txt
    private final String FILE_PATH = "src/main/resources/libros.txt";

    // Guardar libro en el archivo
    public String guardarLibro(Libro libro) throws IOException {
        // Crear archivo si no existe
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }

        if (buscarPorId(libro.getId()) != null) {
            return "Error: El ID ya existe";
        }

        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(libro.getId() + "," +
                     libro.getNombre() + "," +
                     libro.getAutor() + "," +
                     libro.getEditorial() + "," +
                     libro.getAnioEdicion() + "," +
                     libro.getUbicacion());
            bw.newLine();
        }
        return "Libro guardado correctamente";
    }

    // Buscar libro por ID
    public Libro buscarPorId(String id) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos[0].equals(id)) {
                    Libro libro = new Libro();
                    libro.setId(datos[0]);
                    libro.setNombre(datos[1]);
                    libro.setAutor(datos[2]);
                    libro.setEditorial(datos[3]);
                    libro.setAnioEdicion(Integer.parseInt(datos[4]));
                    libro.setUbicacion(datos[5]);
                    return libro;
                }
            }
        }
        return null;
    }
}
