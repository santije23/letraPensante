package com.example.letrapensante.controlers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.letrapensante.Libro;
import com.example.letrapensante.service.LibroService;

@RestController
@RequestMapping("/libros")
public class LibroControler {
    @Autowired
    private LibroService service;

    // Guardar libro
    @PostMapping("/guardar")
    public String guardar(@RequestBody Libro libro) {
        try {
            return service.guardarLibro(libro);
        } catch (IOException e) {
            return "Error al guardar: " + e.getMessage();
        }
    }

    // Buscar libro por ID
    @GetMapping("/buscar/{id}")
    public Object buscar(@PathVariable String id) {
        try {
            Libro libro = service.buscarPorId(id);
            if (libro == null) return "No se encontró libro con ID " + id;
            return libro;
        } catch (IOException e) {
            return "Error al buscar: " + e.getMessage();
        }
    }

}
