package com.example.letrapensante.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.letrapensante.model.Libro;

@Service
public class ArchivoExcel {

    private File archivo = new File("BD.xlsx");

    // Crear archivo con encabezados en ambas hojas
    public String crearArchivo(){
        if(archivo.exists()){
            return "El archivo ya existe.";
        }

        try (Workbook libroExcel = new XSSFWorkbook()) {

            Sheet hojaLibros = libroExcel.createSheet("Libros");
            // Hoja índice principal por ID
            Sheet hojaIndice = libroExcel.createSheet("Indice");
            // Índices adicionales por criterio (nombre, autor, editorial, anio, ubicacion)
            Sheet idxNombre = libroExcel.createSheet("Indice_Nombre");
            Sheet idxAutor = libroExcel.createSheet("Indice_Autor");
            Sheet idxEditorial = libroExcel.createSheet("Indice_Editorial");
            Sheet idxAnio = libroExcel.createSheet("Indice_Anio");
            Sheet idxUbicacion = libroExcel.createSheet("Indice_Ubicacion");

            // Encabezados hoja Libros
            Row encL = hojaLibros.createRow(0);
            encL.createCell(0).setCellValue("ID");
            encL.createCell(1).setCellValue("Nombre");
            encL.createCell(2).setCellValue("Autor");
            encL.createCell(3).setCellValue("Editorial");
            encL.createCell(4).setCellValue("Año");
            encL.createCell(5).setCellValue("Ubicación");

            // Encabezados hoja Índice (ID)
            Row encI = hojaIndice.createRow(0);
            encI.createCell(0).setCellValue("Clave(ID)");
            encI.createCell(1).setCellValue("Dirección(Fila)");

            // Encabezados para los índices por campo
            Row encN = idxNombre.createRow(0);
            encN.createCell(0).setCellValue("Clave(Nombre)");
            encN.createCell(1).setCellValue("Dirección(Fila)");

            Row encA = idxAutor.createRow(0);
            encA.createCell(0).setCellValue("Clave(Autor)");
            encA.createCell(1).setCellValue("Dirección(Fila)");

            Row encE = idxEditorial.createRow(0);
            encE.createCell(0).setCellValue("Clave(Editorial)");
            encE.createCell(1).setCellValue("Dirección(Fila)");

            Row encY = idxAnio.createRow(0);
            encY.createCell(0).setCellValue("Clave(Año)");
            encY.createCell(1).setCellValue("Dirección(Fila)");

            Row encU = idxUbicacion.createRow(0);
            encU.createCell(0).setCellValue("Clave(Ubicación)");
            encU.createCell(1).setCellValue("Dirección(Fila)");

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                libroExcel.write(fos);
            }

            return "Archivo Excel creado correctamente.";
        } catch (IOException e) {
            return "Error al crear el archivo: " + e.getMessage();
        }
    }

    // Insertar registro
    public String insertarRegistro(Libro libroData){

        if (buscarRegistro(libroData.getId()) != null) {
            return "El registro con ID " + libroData.getId() + " ya existe.";
        }

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libroExcel = new XSSFWorkbook(fis)) {

            Sheet hojaLibros = libroExcel.getSheet("Libros");
            Sheet hojaIndice = libroExcel.getSheet("Indice");

            // Asegurar que existan las hojas índice por campo
            ensureIndexSheets(libroExcel);

            // Obtener referencias a índices por campo
            Sheet idxNombre = libroExcel.getSheet("Indice_Nombre");
            Sheet idxAutor = libroExcel.getSheet("Indice_Autor");
            Sheet idxEditorial = libroExcel.getSheet("Indice_Editorial");
            Sheet idxAnio = libroExcel.getSheet("Indice_Anio");
            Sheet idxUbicacion = libroExcel.getSheet("Indice_Ubicacion");

            int filaNueva = hojaLibros.getLastRowNum() + 1;

            Row fila = hojaLibros.createRow(filaNueva);
            fila.createCell(0).setCellValue(libroData.getId());
            fila.createCell(1).setCellValue(libroData.getNombre());
            fila.createCell(2).setCellValue(libroData.getAutor());
            fila.createCell(3).setCellValue(libroData.getEditorial());
            fila.createCell(4).setCellValue(libroData.getAnioEdicion());
            fila.createCell(5).setCellValue(libroData.getUbicacion());

            int filaIndice = hojaIndice.getLastRowNum() + 1;
            Row filaIndex = hojaIndice.createRow(filaIndice);
            filaIndex.createCell(0).setCellValue(libroData.getId());
            filaIndex.createCell(1).setCellValue(filaNueva);

            // Añadir entradas en los índices por campo (permitir duplicados de clave)
            if (idxNombre != null) {
                int r = idxNombre.getLastRowNum() + 1;
                Row f = idxNombre.createRow(r);
                f.createCell(0).setCellValue(libroData.getNombre());
                f.createCell(1).setCellValue(filaNueva);
            }
            if (idxAutor != null) {
                int r = idxAutor.getLastRowNum() + 1;
                Row f = idxAutor.createRow(r);
                f.createCell(0).setCellValue(libroData.getAutor());
                f.createCell(1).setCellValue(filaNueva);
            }
            if (idxEditorial != null) {
                int r = idxEditorial.getLastRowNum() + 1;
                Row f = idxEditorial.createRow(r);
                f.createCell(0).setCellValue(libroData.getEditorial());
                f.createCell(1).setCellValue(filaNueva);
            }
            if (idxAnio != null) {
                int r = idxAnio.getLastRowNum() + 1;
                Row f = idxAnio.createRow(r);
                f.createCell(0).setCellValue(libroData.getAnioEdicion());
                f.createCell(1).setCellValue(filaNueva);
            }
            if (idxUbicacion != null) {
                int r = idxUbicacion.getLastRowNum() + 1;
                Row f = idxUbicacion.createRow(r);
                f.createCell(0).setCellValue(libroData.getUbicacion());
                f.createCell(1).setCellValue(filaNueva);
            }

            // Ordenar todos los índices
            ordenarIndice(hojaIndice);
            if (idxNombre != null) ordenarIndice(idxNombre);
            if (idxAutor != null) ordenarIndice(idxAutor);
            if (idxEditorial != null) ordenarIndice(idxEditorial);
            if (idxAnio != null) ordenarIndice(idxAnio);
            if (idxUbicacion != null) ordenarIndice(idxUbicacion);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                libroExcel.write(fos);
            }

            return "Registro agregado correctamente.";
        } catch (IOException e) {
            return "Error al insertar: " + e.getMessage();
        }
    }

    // Asegura que el workbook tenga las hojas de índice necesarias y sus encabezados
    private void ensureIndexSheets(Workbook libroExcel) {
        if (libroExcel.getSheet("Indice") == null) {
            Sheet s = libroExcel.createSheet("Indice");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(ID)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }

        if (libroExcel.getSheet("Indice_Nombre") == null) {
            Sheet s = libroExcel.createSheet("Indice_Nombre");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(Nombre)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }

        if (libroExcel.getSheet("Indice_Autor") == null) {
            Sheet s = libroExcel.createSheet("Indice_Autor");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(Autor)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }

        if (libroExcel.getSheet("Indice_Editorial") == null) {
            Sheet s = libroExcel.createSheet("Indice_Editorial");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(Editorial)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }

        if (libroExcel.getSheet("Indice_Anio") == null) {
            Sheet s = libroExcel.createSheet("Indice_Anio");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(Año)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }

        if (libroExcel.getSheet("Indice_Ubicacion") == null) {
            Sheet s = libroExcel.createSheet("Indice_Ubicacion");
            Row r = s.createRow(0);
            r.createCell(0).setCellValue("Clave(Ubicación)");
            r.createCell(1).setCellValue("Dirección(Fila)");
        }
    }

    private void ordenarIndice(Sheet hojaIndice) {

        List<String> claves = new ArrayList<>();
        List<Integer> direcciones = new ArrayList<>();

        // Leer filas válidas (desde 1 porque la 0 es encabezado)
        for (int i = 1; i <= hojaIndice.getLastRowNum(); i++) {
            Row fila = hojaIndice.getRow(i);
            if (fila == null) continue;

            Cell celdaClave = fila.getCell(0);
            Cell celdaDireccion = fila.getCell(1);

            if (celdaClave == null || celdaDireccion == null) continue;

            claves.add(celdaClave.getStringCellValue());
            direcciones.add((int) celdaDireccion.getNumericCellValue());
        }

        // Ordenar por clave (ID)
        for (int i = 0; i < claves.size() - 1; i++) {
            for (int j = i + 1; j < claves.size(); j++) {
                if (claves.get(i).compareTo(claves.get(j)) > 0) {
                    String tempClave = claves.get(i);
                    claves.set(i, claves.get(j));
                    claves.set(j, tempClave);

                    int tempDir = direcciones.get(i);
                    direcciones.set(i, direcciones.get(j));
                    direcciones.set(j, tempDir);
                }
            }
        }

        // Reescribir desde fila 1 (dejar encabezado intacto)
        for (int i = 0; i < claves.size(); i++) {
            Row fila = hojaIndice.getRow(i + 1);
            if (fila == null) fila = hojaIndice.createRow(i + 1);

            fila.createCell(0).setCellValue(claves.get(i));
            fila.createCell(1).setCellValue(direcciones.get(i));
        }

        //Elimina filas sobrantes si quedaron vacías
        for (int i = claves.size() + 1; i <= hojaIndice.getLastRowNum(); i++) {
            Row fila = hojaIndice.getRow(i);
            if (fila != null) {
                hojaIndice.removeRow(fila);
            }
        }
    }

    public Libro buscarRegistro(String idBuscado){
        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libroExcel = new XSSFWorkbook(fis)) {

            Sheet hojaIndice = libroExcel.getSheet("Indice");
            Sheet hojaLibros = libroExcel.getSheet("Libros");

            // Asegurar índices por campo
            ensureIndexSheets(libroExcel);

            for (int i = 1; i <= hojaIndice.getLastRowNum(); i++) { // saltar encabezado
                Row fila = hojaIndice.getRow(i);
                if (fila == null || fila.getCell(0) == null) continue;

                if (fila.getCell(0).getStringCellValue().equals(idBuscado)) {

                    int direccion = (int) fila.getCell(1).getNumericCellValue();
                    Row filaLibro = hojaLibros.getRow(direccion);

                    return new Libro(
                        filaLibro.getCell(0).getStringCellValue(),
                        filaLibro.getCell(1).getStringCellValue(),
                        filaLibro.getCell(2).getStringCellValue(),
                        filaLibro.getCell(3).getStringCellValue(),
                        filaLibro.getCell(4).getStringCellValue(),
                        filaLibro.getCell(5).getStringCellValue()
                    );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Listar todos los registros de la hoja "Libros"
    public List<Libro> listarRegistros() {
        List<Libro> resultados = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libroExcel = new XSSFWorkbook(fis)) {

            Sheet hojaLibros = libroExcel.getSheet("Libros");
            if (hojaLibros == null) return resultados;

            int lastRow = hojaLibros.getLastRowNum();

            // Empezar en 1 para saltar encabezado
            for (int i = 1; i <= lastRow; i++) {
                Row fila = hojaLibros.getRow(i);
                if (fila == null) continue;

                Cell c0 = fila.getCell(0);
                Cell c1 = fila.getCell(1);
                Cell c2 = fila.getCell(2);
                Cell c3 = fila.getCell(3);
                Cell c4 = fila.getCell(4);
                Cell c5 = fila.getCell(5);

                if (c0 == null) continue; // sin id no procesar

                String id = c0.getStringCellValue();
                String nombre = c1 != null ? c1.getStringCellValue() : "";
                String autor = c2 != null ? c2.getStringCellValue() : "";
                String editorial = c3 != null ? c3.getStringCellValue() : "";
                String anio = c4 != null ? c4.getStringCellValue() : "";
                String ubicacion = c5 != null ? c5.getStringCellValue() : "";

                resultados.add(new Libro(id, nombre, autor, editorial, anio, ubicacion));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    // Buscar por campos usando filtros opcionales: si un campo está presente en el map,
    // se busca que su valor esté contenido (case-insensitive) en el campo correspondiente.
    public List<Libro> buscarPorCampos(java.util.Map<String, String> filtros) {
        List<Libro> resultados = new ArrayList<>();
        if (filtros == null || filtros.isEmpty()) return resultados;

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libroExcel = new XSSFWorkbook(fis)) {

            Sheet hojaLibros = libroExcel.getSheet("Libros");
            if (hojaLibros == null) return resultados;

            int lastRow = hojaLibros.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row fila = hojaLibros.getRow(i);
                if (fila == null) continue;

                String id = getCellAsString(fila.getCell(0)).toLowerCase();
                String nombre = getCellAsString(fila.getCell(1)).toLowerCase();
                String autor = getCellAsString(fila.getCell(2)).toLowerCase();
                String editorial = getCellAsString(fila.getCell(3)).toLowerCase();
                String anio = getCellAsString(fila.getCell(4)).toLowerCase();
                String ubicacion = getCellAsString(fila.getCell(5)).toLowerCase();

                boolean match = true;

                for (java.util.Map.Entry<String, String> e : filtros.entrySet()) {
                    String key = e.getKey();
                    String val = e.getValue();
                    if (val == null) continue;
                    String q = val.toLowerCase();

                    switch (key) {
                        case "id":
                            if (!id.contains(q)) match = false;
                            break;
                        case "nombre":
                            if (!nombre.contains(q)) match = false;
                            break;
                        case "autor":
                            if (!autor.contains(q)) match = false;
                            break;
                        case "editorial":
                            if (!editorial.contains(q)) match = false;
                            break;
                        case "anio":
                            if (!anio.contains(q)) match = false;
                            break;
                        case "ubicacion":
                            if (!ubicacion.contains(q)) match = false;
                            break;
                        default:
                            // ignorar claves desconocidas
                            break;
                    }

                    if (!match) break; // si falla un filtro, no hace falta seguir
                }

                if (match) {
                    resultados.add(new Libro(getCellAsString(fila.getCell(0)), getCellAsString(fila.getCell(1)), getCellAsString(fila.getCell(2)), getCellAsString(fila.getCell(3)), getCellAsString(fila.getCell(4)), getCellAsString(fila.getCell(5))));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    // Helper para obtener valor de celda como String (maneja numeric, boolean y formula)
    private String getCellAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) {
                    return String.valueOf((long) d);
                } else {
                    return String.valueOf(d);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); }
                catch (Exception ex) { return String.valueOf(cell.getNumericCellValue()); }
            default:
                return "";
        }
    }

    public void eliminarArchivo(){
        if (archivo.delete()) {
            System.out.println("BD.xlsx eliminada correctamente");
        } else {
            System.out.println("No se pudo eliminar el archivo");
        }
    }

    // Eliminar registro por ID: borra la fila en 'Libros' y actualiza la hoja 'Indice'
    public String eliminarRegistro(String idBuscado) {
        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libroExcel = new XSSFWorkbook(fis)) {

            Sheet hojaIndice = libroExcel.getSheet("Indice");
            Sheet hojaLibros = libroExcel.getSheet("Libros");

            int filaIndiceEncontrada = -1;
            int direccion = -1;

            for (int i = 1; i <= hojaIndice.getLastRowNum(); i++) {
                Row fila = hojaIndice.getRow(i);
                if (fila == null || fila.getCell(0) == null) continue;

                if (fila.getCell(0).getStringCellValue().equals(idBuscado)) {
                    filaIndiceEncontrada = i;
                    direccion = (int) fila.getCell(1).getNumericCellValue();
                    break;
                }
            }

            if (filaIndiceEncontrada == -1) {
                return "Registro con ID " + idBuscado + " no encontrado.";
            }

            // Leer valores del libro a eliminar antes de borrar la fila
            Row filaLibro = hojaLibros.getRow(direccion);
            String nombreVal = "";
            String autorVal = "";
            String editorialVal = "";
            String anioVal = "";
            String ubicacionVal = "";
            if (filaLibro != null) {
                nombreVal = filaLibro.getCell(1) != null ? filaLibro.getCell(1).getStringCellValue() : "";
                autorVal = filaLibro.getCell(2) != null ? filaLibro.getCell(2).getStringCellValue() : "";
                editorialVal = filaLibro.getCell(3) != null ? filaLibro.getCell(3).getStringCellValue() : "";
                anioVal = filaLibro.getCell(4) != null ? filaLibro.getCell(4).getStringCellValue() : "";
                ubicacionVal = filaLibro.getCell(5) != null ? filaLibro.getCell(5).getStringCellValue() : "";
            }

            // Eliminar fila en hoja Libros
            if (filaLibro != null) {
                hojaLibros.removeRow(filaLibro);
            }

            int lastRowLib = hojaLibros.getLastRowNum();
            if (direccion < lastRowLib) {
                // Mover filas hacia arriba para rellenar el hueco
                hojaLibros.shiftRows(direccion + 1, lastRowLib, -1);
            }

            // Eliminar entrada en Indice (ID)
            Row filaIdx = hojaIndice.getRow(filaIndiceEncontrada);
            if (filaIdx != null) {
                hojaIndice.removeRow(filaIdx);
            }

            // También eliminar las entradas correspondientes en los índices por campo
            Sheet idxNombre = libroExcel.getSheet("Indice_Nombre");
            Sheet idxAutor = libroExcel.getSheet("Indice_Autor");
            Sheet idxEditorial = libroExcel.getSheet("Indice_Editorial");
            Sheet idxAnio = libroExcel.getSheet("Indice_Anio");
            Sheet idxUbicacion = libroExcel.getSheet("Indice_Ubicacion");

            // Eliminar entradas correspondientes en cada índice por campo (coincidencia por clave y dirección)
            if (idxNombre != null) {
                for (int i = 1; i <= idxNombre.getLastRowNum(); i++) {
                    Row f = idxNombre.getRow(i);
                    if (f == null || f.getCell(0) == null || f.getCell(1) == null) continue;
                    String key = f.getCell(0).getStringCellValue();
                    int dir = (int) f.getCell(1).getNumericCellValue();
                    if (key.equals(nombreVal) && dir == direccion) { idxNombre.removeRow(f); break; }
                }
            }
            if (idxAutor != null) {
                for (int i = 1; i <= idxAutor.getLastRowNum(); i++) {
                    Row f = idxAutor.getRow(i);
                    if (f == null || f.getCell(0) == null || f.getCell(1) == null) continue;
                    String key = f.getCell(0).getStringCellValue();
                    int dir = (int) f.getCell(1).getNumericCellValue();
                    if (key.equals(autorVal) && dir == direccion) { idxAutor.removeRow(f); break; }
                }
            }
            if (idxEditorial != null) {
                for (int i = 1; i <= idxEditorial.getLastRowNum(); i++) {
                    Row f = idxEditorial.getRow(i);
                    if (f == null || f.getCell(0) == null || f.getCell(1) == null) continue;
                    String key = f.getCell(0).getStringCellValue();
                    int dir = (int) f.getCell(1).getNumericCellValue();
                    if (key.equals(editorialVal) && dir == direccion) { idxEditorial.removeRow(f); break; }
                }
            }
            if (idxAnio != null) {
                for (int i = 1; i <= idxAnio.getLastRowNum(); i++) {
                    Row f = idxAnio.getRow(i);
                    if (f == null || f.getCell(0) == null || f.getCell(1) == null) continue;
                    String key = f.getCell(0).getStringCellValue();
                    int dir = (int) f.getCell(1).getNumericCellValue();
                    if (key.equals(anioVal) && dir == direccion) { idxAnio.removeRow(f); break; }
                }
            }
            if (idxUbicacion != null) {
                for (int i = 1; i <= idxUbicacion.getLastRowNum(); i++) {
                    Row f = idxUbicacion.getRow(i);
                    if (f == null || f.getCell(0) == null || f.getCell(1) == null) continue;
                    String key = f.getCell(0).getStringCellValue();
                    int dir = (int) f.getCell(1).getNumericCellValue();
                    if (key.equals(ubicacionVal) && dir == direccion) { idxUbicacion.removeRow(f); break; }
                }
            }

            // Ajustar direcciones en TODOS los índices: decrementar en 1 las direcciones mayores a la eliminada
            java.util.List<Sheet> allIdx = new java.util.ArrayList<>();
            allIdx.add(hojaIndice);
            if (idxNombre != null) allIdx.add(idxNombre);
            if (idxAutor != null) allIdx.add(idxAutor);
            if (idxEditorial != null) allIdx.add(idxEditorial);
            if (idxAnio != null) allIdx.add(idxAnio);
            if (idxUbicacion != null) allIdx.add(idxUbicacion);

            for (Sheet s : allIdx) {
                for (int i = 1; i <= s.getLastRowNum(); i++) {
                    Row f = s.getRow(i);
                    if (f == null || f.getCell(1) == null) continue;
                    double val = f.getCell(1).getNumericCellValue();
                    int dir = (int) val;
                    if (dir > direccion) {
                        f.getCell(1).setCellValue(dir - 1);
                    }
                }
            }

            // Reordenar todos los índices para mantener consistencia
            ordenarIndice(hojaIndice);
            if (idxNombre != null) ordenarIndice(idxNombre);
            if (idxAutor != null) ordenarIndice(idxAutor);
            if (idxEditorial != null) ordenarIndice(idxEditorial);
            if (idxAnio != null) ordenarIndice(idxAnio);
            if (idxUbicacion != null) ordenarIndice(idxUbicacion);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                libroExcel.write(fos);
            }

            return "Registro con ID " + idBuscado + " eliminado correctamente.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al eliminar: " + e.getMessage();
        }
    }

}
