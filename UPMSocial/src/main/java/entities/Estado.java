package entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;

@XmlRootElement(name = "estado")
public class Estado {
	private int id;
	private int usuario_id;
	private String contenido;
	private String autor;
	private Date fecha;

	public Estado(String contenido) {
		this.contenido = contenido;
	}

	public Estado() {
	}

	@XmlAttribute(required = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}

	public String toString() {
		return this.contenido.replace(" ","");
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

}
