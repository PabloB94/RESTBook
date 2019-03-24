package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usuarios")
public class ListaUsuarios {
	private ArrayList<Usuario> usuarios;

	public ListaUsuarios() {
		this.usuarios = new ArrayList<Usuario>();
	}
	
	public ListaUsuarios(ArrayList<Usuario> usuario) {
		this.usuarios = usuario;
	}


	@XmlElement(name="usuario")
	public ArrayList<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(ArrayList<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
}