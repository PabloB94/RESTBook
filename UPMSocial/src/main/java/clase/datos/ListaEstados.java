package clase.datos;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "estados")
public class ListaEstados {
	private ArrayList<Estado> estados;

	public ListaEstados() {
		this.estados = new ArrayList<Estado>();
	}

	public ListaEstados(ArrayList<Estado> estados) {
		this.estados = estados;
	}

	@XmlElement(name="estado")
	public ArrayList<Estado> getEstados() {
		return estados;
	}

	public void setEstados(ArrayList<Estado> estados) {
		this.estados = estados;
	}

}
