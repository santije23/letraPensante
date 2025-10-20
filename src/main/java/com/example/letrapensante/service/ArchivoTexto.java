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
    private Node root;

    private static class Node {
        private String key;
        private Libro value;
        private Node left;
        private Node right;

        Node(String key, Libro value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private String generateKey(Libro libro) {
        return libro.getId() + "-" + libro.getNombre().toLowerCase();
    }

    private Node insert(Node node, String key, Libro libro) {
        if (node == null) {
            return new Node(key, libro);
        }

        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = insert(node.left, key, libro);
        } else if (comparison > 0) {
            node.right = insert(node.right, key, libro);
        } else {
            node.value = libro; // Update value if key already exists
        }
        return node;
    }

    private Node search(Node node, String key) {
        if (node == null || node.key.equals(key)) {
            return node;
        }

        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            return search(node.left, key);
        }
        return search(node.right, key);
    }

    private void saveTree(Node node, FileWriter writer) throws IOException {
        if (node != null) {
            saveTree(node.left, writer);
            Libro libro = node.value;
            writer.write(libro.getId() + "," +
                        libro.getNombre() + "," +
                        libro.getAutor() + "," +
                        libro.getEditorial() + "," +
                        libro.getAnioEdicion() + "," +
                        libro.getUbicacion() + "\n");
            saveTree(node.right, writer);
        }
    }
    

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
        if (root == null) {
            cargarArbol();
        }

        Node result = searchByPrefix(root, idBuscado + "-");
        if (result != null) {
            System.out.println("Registro encontrado: " + result.value.getNombre());
            return result.value;
        }
        return null;
    }

    private Node searchByPrefix(Node node, String prefix) {
        if (node == null) {
            return null;
        }

        if (node.key.startsWith(prefix)) {
            return node;
        }

        int comparison = prefix.compareTo(node.key);
        if (comparison < 0) {
            return searchByPrefix(node.left, prefix);
        }
        return searchByPrefix(node.right, prefix);
    }

    public String insertarRegistro(Libro libro){
        if (!validarLibro(libro)) {
            return "Error: datos inválidos, por favor verifique los campos.";
        }
        if (root == null) {
            cargarArbol();
        }
        String key = generateKey(libro);
        Node existing = search(root, key);
        if (existing != null) {
            return "El registro con ID " + libro.getId() + " ya existe.";
        }

        root = insert(root, key, libro);
        numeroRegistros++;

        
        try (FileWriter writer = new FileWriter(archivo)) {
            saveTree(root, writer);
            return "Registro " + numeroRegistros + " insertado correctamente";
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return "Error al insertar";
        }
    }

    private void cargarArbol() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            root = null;
            numeroRegistros = 0;
            
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 6) {
                    Libro libro = new Libro(
                        datos[0], // id
                        datos[1], // nombre
                        datos[2], // autor
                        datos[3], // editorial
                        datos[4], // año edición
                        datos[5]  // ubicación
                    );
                    String key = generateKey(libro);
                    root = insert(root, key, libro);
                    numeroRegistros++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    private boolean validarLibro(Libro libro) {
        if (libro.getId() == null || libro.getId().trim().isEmpty()) return false;
        if (libro.getNombre() == null || libro.getNombre().trim().isEmpty()) return false;
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) return false;
        if (libro.getEditorial() == null || libro.getEditorial().trim().isEmpty()) return false;
        if (libro.getUbicacion() == null || libro.getUbicacion().trim().isEmpty()) return false;
        try {
            int anio = Integer.parseInt(libro.getAnioEdicion());
            if (anio <= 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

