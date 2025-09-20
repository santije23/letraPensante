package com.example.letrapensante.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; 
import java.nio.file.StandardCopyOption; 
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.letrapensante.model.Libro;

import jakarta.annotation.PostConstruct;

@Service
public class ArchivoTexto {

    private File archivo;
    private int numeroRegistros = 0;
    private final String NOMBRE_ARCHIVO = "BD.txt";

    @PostConstruct
    public void init() {
        try {
            // Ruta real donde se pueda escribir (funciona local y en Azure)
            String rutaArchivoReal = System.getProperty("user.dir") + File.separator + NOMBRE_ARCHIVO;
            archivo = new File(rutaArchivoReal);

            if (!archivo.exists()) {
                // Copiar desde resources si no existe
                ClassPathResource resource = new ClassPathResource(NOMBRE_ARCHIVO);
                InputStream is = resource.getInputStream();
                Files.copy(is, archivo.toPath());
                is.close();
                System.out.println("Archivo BD.txt copiado desde resources a: " + archivo.getAbsolutePath());
            } else {
                System.out.println("Archivo ya existe en: " + archivo.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String crearArchivo() {
        try {
            if (archivo.createNewFile()) {
                return "Archivo creado correctamente";
            } else {
                return "El archivo ya existe";
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return "Error al crear el archivo: " + e.getMessage();
        }
    }

    public void eliminarArchivo() {
        if (archivo.delete()) {
            System.out.println("BD eliminada");
        } else {
            System.out.println("Error al eliminar");
        }
    }

    public Libro buscarRegistro(String idBuscado) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 6 && datos[0].equals(idBuscado)) {
                    Libro libro = new Libro(
                        datos[0], // id
                        datos[1], // nombre
                        datos[2], // autor
                        datos[3], // editorial
                        datos[4], // año edición
                        datos[5]  // ubicación
                    );
                    System.out.println("Registro encontrado: " + libro.getNombre());
                    return libro;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public String insertarRegistro(Libro libro) {
        if (buscarRegistro(libro.getId()) != null) {
            return "El registro con ID " + libro.getId() + " ya existe.";
        }
        try (FileWriter insertar = new FileWriter(archivo, true)) {
            insertar.write(libro.getId() + "," +
                    libro.getNombre() + "," +
                    libro.getAutor() + "," +
                    libro.getEditorial() + "," +
                    libro.getAnioEdicion() + "," +
                    libro.getUbicacion() + "\n");

            numeroRegistros++;
            return "Registro " + numeroRegistros + " insertado correctamente";

        } catch (IOException e) {
            e.printStackTrace(System.out);
            return "Error al insertar";
        }
    }
}
