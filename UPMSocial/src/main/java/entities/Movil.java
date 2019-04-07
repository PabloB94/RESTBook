package entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movil")
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(propOrder= {"usuario","ultimoEstado","nAmigos","estadoAmigos"})
public class Movil {

	private Usuario usuario;
	private int nAmigos;
	private Estado ultimoEstado;
	private ListaEstados estadosAmigos;

	public Movil() {
		nAmigos=0;
	}

	@XmlElement(name = "estado")
	public Estado getUltimoEstado() {
		return ultimoEstado;
	}

	public void setUltimoEstado(Estado ultimoEstado) {
		this.ultimoEstado = ultimoEstado;
	}

	@XmlElement(name = "ListaEstados")
	public ListaEstados getEstadosAmigos() {
		return estadosAmigos;
	}

	public void setEstadosAmigos(ListaEstados estadosAmigos) {
		this.estadosAmigos = estadosAmigos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getnAmigos() {
		return nAmigos;
	}

	public void setnAmigos(int nAmigos) {
		this.nAmigos = nAmigos;
	}

}
