package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;

public class Amigo {

	private int id;
	private String amigo;
	private int idUsuario;

	public Amigo() {

	}

	public Amigo(String amigo, int idUsuario) {
		this.amigo = amigo;
		this.idUsuario = idUsuario;
	}

	public String getAmigo() {
		return amigo;
	}

	public void setAmigo(String amigo) {
		this.amigo = amigo;
	}

	public int getidUsuario() {
		return idUsuario;
	}

	public void setidUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	@XmlAttribute(required = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
