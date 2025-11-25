package com.example.letrapensante.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
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
@CrossOrigin(origins = "http://localhost:4200",
             allowedHeaders = "*",
             allowCredentials = "true",
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class LibroController {

    @Autowired
    private ArchivoExcel archivoExcel; 

    @PostMapping
    public String insertar(@RequestBody Libro libro) {
        return archivoExcel.insertarRegistro(libro); 
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String nombre,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String autor,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String editorial,
            @org.springframework.web.bind.annotation.RequestParam(required = false, name = "anio") String anio,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String ubicacion
    ) {
        try {
            java.util.Map<String, String> filtros = new java.util.HashMap<>();
            if (id != null && !id.isEmpty()) filtros.put("id", id);
            if (nombre != null && !nombre.isEmpty()) filtros.put("nombre", nombre);
            if (autor != null && !autor.isEmpty()) filtros.put("autor", autor);
            if (editorial != null && !editorial.isEmpty()) filtros.put("editorial", editorial);
            if (anio != null && !anio.isEmpty()) filtros.put("anio", anio);
            if (ubicacion != null && !ubicacion.isEmpty()) filtros.put("ubicacion", ubicacion);

            if (filtros.isEmpty()) {
                java.util.List<Libro> lista = archivoExcel.listarRegistros();
                return ResponseEntity.ok(lista);
            } else {
                java.util.List<Libro> lista = archivoExcel.buscarPorCampos(filtros);
                return ResponseEntity.ok(lista);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener registros: " + e.getMessage());
        }
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        String resultado = archivoExcel.eliminarRegistro(id);
        if (resultado.startsWith("Registro con ID") && resultado.contains("eliminado correctamente")) {
            return ResponseEntity.ok(resultado);
        } else if (resultado.startsWith("Registro con ID") && resultado.contains("no encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        } else if (resultado.startsWith("Error al eliminar")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
        } else {
            // Mensaje genérico por defecto
            return ResponseEntity.ok(resultado);
        }
    }

    @PostMapping("/crearArchivo")
    public String crearArchivo() {
        return archivoExcel.crearArchivo(); 
    }
}
