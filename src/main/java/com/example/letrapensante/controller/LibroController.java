package com.example.letrapensante.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.letrapensante.model.Libro;
import com.example.letrapensante.service.ArchivoExcel;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private ArchivoExcel archivoExcel; 

    @PostMapping
    public String insertar(@RequestBody Libro libro) {
        return archivoExcel.insertarRegistro(libro); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        Libro libro = archivoExcel.buscarRegistro(id); 

        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Registro con ID " + id + " no encontrado.");
        }
    }

    @DeleteMapping
    public String eliminarArchivo() {
        archivoExcel.eliminarArchivo(); 
        return "Archivo eliminado";
    }

    @PostMapping("/crearArchivo")
    public String crearArchivo() {
        return archivoExcel.crearArchivo(); 
    }
}
