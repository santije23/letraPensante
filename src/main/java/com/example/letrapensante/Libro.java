package com.example.letrapensante;

public class Libro {
    private String id;      
    private String nombre;
    private String autor;
    private String editorial;
    private int anioEdicion; 
    private String ubicacion;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getAutor() {
		return this.autor;
	}

	public void setAutor(String Autor) {
		this.autor = Autor;
	}

	public String getEditorial() {
		return this.editorial;
	}

	public void setEditorial(String Editorial) {
		this.editorial = Editorial;
	}

	public int getAnioEdicion() {
		return this.anioEdicion;
	}

	public void setAnioEdicion(int AEdicion) {
		this.anioEdicion = AEdicion;
	}

	public String getUbicacion() {
		return this.ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

}
