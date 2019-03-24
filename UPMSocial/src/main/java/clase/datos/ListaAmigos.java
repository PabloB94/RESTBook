package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "amigos")
public class ListaAmigos {
	private ArrayList<Amigo> amigos;

	public ListaAmigos() {
		this.amigos = new ArrayList<Amigo>();
	}
	
	public ListaAmigos(ArrayList<Amigo> amigos) {
		this.amigos = amigos;
	}


	@XmlElement(name="amigo")
	public ArrayList<Amigo> getAmigos() {
		return amigos;
	}

	public void setAmigos(ArrayList<Amigo> amigos) {
		this.amigos = amigos;
	}

}
