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
            Sheet hojaIndice = libroExcel.createSheet("Indice");

            // Encabezados hoja Libros
            Row encL = hojaLibros.createRow(0);
            encL.createCell(0).setCellValue("ID");
            encL.createCell(1).setCellValue("Nombre");
            encL.createCell(2).setCellValue("Autor");
            encL.createCell(3).setCellValue("Editorial");
            encL.createCell(4).setCellValue("Año");
            encL.createCell(5).setCellValue("Ubicación");

            // Encabezados hoja Índice
            Row encI = hojaIndice.createRow(0);
            encI.createCell(0).setCellValue("Clave(ID)");
            encI.createCell(1).setCellValue("Dirección(Fila)");

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

            ordenarIndice(hojaIndice);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                libroExcel.write(fos);
            }

            return "Registro agregado correctamente.";
        } catch (IOException e) {
            return "Error al insertar: " + e.getMessage();
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

    public void eliminarArchivo(){
        if (archivo.delete()) {
            System.out.println("BD.xlsx eliminada correctamente");
        } else {
            System.out.println("No se pudo eliminar el archivo");
        }
    }

}
