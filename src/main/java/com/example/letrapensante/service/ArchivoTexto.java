package com.example.letrapensante.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.letrapensante.model.Libro;


@Service
public class ArchivoTexto {
    private File archivo = new File("BD.txt");
    private int numeroRegistros=0;
    

    public String crearArchivo(){
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

    public void eliminarArchivo(){
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
                    
                    Libro libro = new Libro(datos[0], // id
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

    public String insertarRegistro(Libro libro){
        if (buscarRegistro(libro.getId()) != null) {
            return "El registro con ID " + libro.getId() + " ya existe.";
        }
        try (FileWriter insertar = new FileWriter(archivo, true);){
            insertar.write(libro.getId() + "," +
                            libro.getNombre() + "," +
                            libro.getAutor() + "," +
                            libro.getEditorial() + "," +
                            libro.getAnioEdicion() + "," +
                            libro.getUbicacion() + "\n");

            numeroRegistros=numeroRegistros+1;
            return "Registro " + numeroRegistros + " insertado correctamente";
            
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return "Error al insertar";
        }
    }
}

