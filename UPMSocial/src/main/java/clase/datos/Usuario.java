package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usuario")
public class Usuario {
	private int id;
	private String nombre;
	private String descripcion;
	private String correo;

	public Usuario(/* int id, */ String nombre, String descripcion, String correo) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.correo = correo;
	}

	public Usuario() {
	}

	@XmlAttribute(required = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	//@XmlTransient
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

}
