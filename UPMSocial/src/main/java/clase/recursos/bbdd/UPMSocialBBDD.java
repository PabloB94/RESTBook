package clase.recursos.bbdd;

import clase.datos.*;
import clase.datos.Amigo;
import clase.datos.Estado;
import org.apache.naming.NamingContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("usuarios")
public class UPMSocialBBDD {

	@Context
	private UriInfo uriInfo;

	private Connection conn;

	public void conectar() throws NamingException, SQLException {
		DataSource ds;
		InitialContext ctx = new InitialContext();
		NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");
		ds = (DataSource) envCtx.lookup("jdbc/myDeposdb");
		conn = ds.getConnection();
	}

	// Devuelve la lista de usuarios
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getListaUsuarios() {
		try {
			conectar();
			String sql = "SELECT * FROM Usuario;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Usuario usuario = new Usuario();
			ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
			try {
				while (rs.next()) {
					usuario = new Usuario();
					usuario.setNombre(rs.getString("nombre"));
					usuario.setDescripcion(rs.getString("descripcion"));
					usuario.setCorreo(rs.getString("correo"));
					usuario.setId(rs.getInt("id"));
					usuarios.add(usuario);
				}
				conn.close();
				return Response.ok(new ListaUsuarios(usuarios)).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).entity("Error, no hay nadie en la base de datos")
						.build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	// Devuelve los datos de un usuario
	@Path("{id}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDatosUsuario(@PathParam("id") String id) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Usuario where id=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			try {
				rs.next();
				Usuario usuario = new Usuario();
				usuario.setNombre(rs.getString("nombre"));
				usuario.setDescripcion(rs.getString("descripcion"));
				usuario.setCorreo(rs.getString("correo"));
				usuario.setId(rs.getInt("id"));
				conn.close();
				return Response.ok(usuario).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).entity("Error, no se encontro a: " + id).build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postCrearUsuario(JAXBElement<Usuario> usuario) {
		try {
			conectar();
			String sql = "SELECT * FROM Usuario where nombre=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, usuario.getValue().getNombre());
			ResultSet rs = ps.executeQuery();
			Usuario usuarioaux = null;
			if (rs.next()) {
				usuarioaux = new Usuario();
				usuarioaux.setNombre(rs.getString("nombre"));
				usuarioaux.setDescripcion(rs.getString("descripcion"));
				usuarioaux.setCorreo(rs.getString("correo"));
				usuarioaux.setId(rs.getInt("id"));
			}
			if (usuarioaux != null) {
				conn.close();
				return Response.status(Response.Status.CONFLICT)
						.entity("Error, el usuario " + usuario.getValue().getNombre() + " ya esta en la el sistema.")
						.build();
			}
			sql = "INSERT INTO Usuario (nombre,descripcion,correo) VALUES (?,?,?);";
			ps = conn.prepareStatement(sql);
			ps.setString(1, usuario.getValue().getNombre());
			ps.setString(2, usuario.getValue().getDescripcion());
			ps.setString(3, usuario.getValue().getCorreo());
			ps.executeUpdate();
			conn.close();
			return Response.status(Status.CREATED)
					.header("Location", uriInfo.getAbsolutePath().toString() + "/" + usuario.getValue().getNombre())
					.build();

		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@GET
	@Path("{id}/estados")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getEstadosUsuario(@PathParam("id") String id,
			@QueryParam("inicio") @DefaultValue("1996-01-25") String inicio,
			@QueryParam("fin") @DefaultValue("2018-05-17") String fin,
			@QueryParam("desde") @DefaultValue("0") int desde, @QueryParam("hasta") @DefaultValue("50") int hasta) {

		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM myDeposdb.Estado where Usuario_id=? AND FECHA BETWEEN ? AND ? ORDER BY fecha ASC LIMIT ?,?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, inicio);
			ps.setString(3, fin);
			ps.setLong(4, desde);
			ps.setLong(5, hasta);
			ResultSet rs = ps.executeQuery();
			Estado estado = null;
			ArrayList<Estado> estados = new ArrayList<Estado>();
			try {
				while (rs.next()) {
					estado = new Estado();
					estado.setContenido(rs.getString("contenido"));
					estado.setAutor(rs.getString("autor"));
					estado.setFecha(rs.getDate("fecha"));
					estado.setId(rs.getInt("id"));
					estado.setUsuario_id(rs.getInt("Usuario_id"));
					estados.add(estado);
				}
				conn.close();
				return Response.ok(new ListaEstados(estados)).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@GET
	@Path("{id}/numeroEstados")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getNumeroEstados(@PathParam("id") String id,
			@QueryParam("inicio") @DefaultValue("1996-01-25") String inicio,
			@QueryParam("fin") @DefaultValue("2018-05-17") String fin) {

		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM myDeposdb.Estado where Usuario_id=? AND FECHA BETWEEN ? AND ? ORDER BY fecha ASC;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, inicio);
			ps.setString(3, fin);
			ResultSet rs = ps.executeQuery();
			int nEstados = 0;
			while (rs.next()) {
				nEstados++;
			}
			return Response.ok(String.valueOf(nEstados)).build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@POST
	@Path("{id}/estados")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postCrearEstado(@PathParam("id") String id, JAXBElement<Estado> estado) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM myDeposdb.Usuario WHERE id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			String autor = null;
			if (rs.next())
				autor = rs.getString("nombre");
			sql = "INSERT INTO Estado (contenido,autor,fecha,Usuario_id) VALUES (?,?,?,?);";
			ps = conn.prepareStatement(sql);
			ps.setString(1, estado.getValue().getContenido());
			ps.setString(2, autor);
			java.util.Date f = new java.util.Date();
			java.sql.Date fecha = new java.sql.Date(f.getTime());
			ps.setString(3, fecha.toString());
			ps.setString(4, id);
			ps.executeUpdate();
			conn.close();
			return Response.status(Status.OK)
					.header("Location", uriInfo.getAbsolutePath().toString() + "/" + estado.getValue().toString())
					.build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@DELETE
	@Path("{id}/estados/{idEstado}")
	public Response deleteEstado(@PathParam("id") String id, @PathParam("idEstado") String idEstado) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "DELETE FROM Estado WHERE Usuario_id= ? and id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, idEstado);
			ps.executeUpdate();
			conn.close();
			return Response.ok().build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@GET
	@Path("{id}/buscarAmigos")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAmigosSugeridos(@PathParam("id") String id,
			@QueryParam("nombre") @DefaultValue("") String nombre) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Usuario where nombre LIKE '%" + nombre + "%';";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Usuario usuario = new Usuario();
			ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
			try {
				while (rs.next()) {
					usuario = new Usuario();
					usuario.setNombre(rs.getString("nombre"));
					usuario.setDescripcion(rs.getString("descripcion"));
					usuario.setCorreo(rs.getString("correo"));
					usuario.setId(rs.getInt("id"));
					usuarios.add(usuario);
				}
				conn.close();
				return Response.ok(new ListaUsuarios(usuarios)).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Error, no hay nadie en el sistema que contenga: " + nombre).build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@POST
	@Path("{id}/amigos")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postCrearAmigo(@PathParam("id") String id, JAXBElement<Usuario> amigo) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Amigo where amigo=? AND Usuario_id=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, amigo.getValue().getNombre());
			ps.setString(2, id);
			ResultSet rs = ps.executeQuery();
			Amigo amigoaux = null;
			if (rs.next()) {
				amigoaux = new Amigo();
				amigoaux.setAmigo(rs.getString("amigo"));
				amigoaux.setidUsuario(rs.getInt("Usuario_id"));
				amigoaux.setId(rs.getInt("id"));
			}
			if (amigoaux != null) {
				conn.close();
				return Response.status(Response.Status.CONFLICT)
						.entity("Error, el usuario: " + amigoaux.getAmigo() + " ya es su amigo.").build();
			}
			sql = "SELECT * FROM Usuario where nombre=?;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, amigo.getValue().getNombre());
			rs = ps.executeQuery();
			if (!rs.next()) {
				conn.close();
				return Response.status(Response.Status.CONFLICT)
						.entity("Error, el usuario: " + amigo.getValue().getNombre() + " no esta en el sistema.")
						.build();
			}
			String idAmigo = rs.getString("id");
			if (id.equals(idAmigo)) {
				conn.close();
				return Response.status(Response.Status.CONFLICT)
						.entity("Error, el usuario no puede ser amigo de si mismo.").build();
			}
			if (!usuarioExiste(idAmigo)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sqlaux = "INSERT INTO myDeposdb.Amigo(amigo,Usuario_id) VALUES(?,?);";
			PreparedStatement psaux = conn.prepareStatement(sqlaux);
			psaux.setString(1, rs.getString("nombre"));
			psaux.setString(2, id);
			psaux.executeUpdate();
			conn.close();
			return Response.status(Status.OK).header("Location", uriInfo.getAbsolutePath().toString() + "/" + idAmigo)
					.build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@DELETE
	@Path("{id}/amigos/{amigo}")
	public Response deleteAmigo(@PathParam("id") String id, @PathParam("amigo") String amigo) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "DELETE FROM Amigo WHERE Usuario_id= ? and amigo = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, amigo);
			ps.executeUpdate();
			conn.close();
			return Response.ok().build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@GET
	@Path("{id}/amigos")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAmigos(@PathParam("id") String id, @QueryParam("nombre") @DefaultValue("") String nombre,
			@QueryParam("desde") @DefaultValue("0") int desde, @QueryParam("hasta") @DefaultValue("50") int hasta) {

		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Amigo WHERE Usuario_id=? AND amigo LIKE '%" + nombre + "%' LIMIT ?,?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setLong(2, desde);
			ps.setLong(3, hasta);
			ResultSet rs = ps.executeQuery();
			Amigo amigo = null;
			ArrayList<Amigo> amigos = new ArrayList<Amigo>();
			try {
				while (rs.next()) {
					amigo = new Amigo();
					amigo.setidUsuario(rs.getInt("Usuario_id"));
					amigo.setAmigo(rs.getString("amigo"));
					amigo.setId(rs.getInt("id"));
					amigos.add(amigo);
				}
				conn.close();
				return Response.ok(new ListaAmigos(amigos)).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putActualizar(JAXBElement<Usuario> usuario) {
		try {
			conectar();
			if (!usuarioExiste(String.valueOf(usuario.getValue().getId()))) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "UPDATE Usuario SET descripcion=?, correo=? WHERE nombre = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, usuario.getValue().getDescripcion());
			ps.setString(2, usuario.getValue().getCorreo());
			ps.setString(3, usuario.getValue().getNombre());
			ps.executeUpdate();
			conn.close();
			return Response.status(Status.OK).header("Location", uriInfo.getAbsolutePath().toString()).build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@DELETE
	@Path("{id}")
	public Response deletUsuario(@PathParam("id") String id) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "DELETE FROM Usuario WHERE id= ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.executeUpdate();
			conn.close();
			return Response.ok().build();
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@Path("{id}/estadoAmigos")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getEstadosAmigos(@PathParam("id") String id,
			@QueryParam("fin") @DefaultValue("2018-05-30") String fin,
			@QueryParam("contenido") @DefaultValue("") String contenido,
			@QueryParam("desde") @DefaultValue("0") int desde, @QueryParam("hasta") @DefaultValue("50") int hasta) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Estado INNER JOIN Amigo ON Estado.autor = Amigo.amigo WHERE Amigo.Usuario_id=? AND Estado.fecha<? AND Estado.contenido LIKE ? LIMIT ?,?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, fin);
			ps.setString(3, "%" + contenido + "%");
			ps.setInt(4, desde);
			ps.setInt(5, hasta);
			ResultSet rs = ps.executeQuery();
			Estado estado = null;
			ArrayList<Estado> estados = new ArrayList<Estado>();
			try {
				while (rs.next()) {
					estado = new Estado();
					estado.setContenido(rs.getString("contenido"));
					estado.setAutor(rs.getString("autor"));
					estado.setFecha(rs.getDate("fecha"));
					estado.setId(rs.getInt("id"));
					estado.setUsuario_id(rs.getInt("Usuario_id"));
					estados.add(estado);
				}
				conn.close();
				return Response.ok(new ListaEstados(estados)).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	@Path("{id}/movil")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDatosMovil(@PathParam("id") String id) {
		try {
			conectar();
			if (!usuarioExiste(id)) {
				conn.close();
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			String sql = "SELECT * FROM Usuario where id=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			Movil movil = new Movil();
			Usuario usuario = new Usuario();
			try {
				rs.next();
				usuario.setNombre(rs.getString("nombre"));
				usuario.setDescripcion(rs.getString("descripcion"));
				usuario.setCorreo(rs.getString("correo"));
				usuario.setId(rs.getInt("id"));
				movil.setUsuario(usuario);
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).entity("Error, no se encontro a: " + id).build();
			}

			sql = "SELECT * FROM Estado where Usuario_id=?;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			try {
				rs.next();
				Estado estado = new Estado();
				estado.setContenido(rs.getString("contenido"));
				estado.setAutor(rs.getString("autor"));
				estado.setFecha(rs.getDate("fecha"));
				estado.setId(rs.getInt("id"));
				estado.setUsuario_id(rs.getInt("usuario_id"));
				movil.setUltimoEstado(estado);
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).entity("Error, no se encontro a: " + id).build();
			}

			sql = "SELECT * FROM Amigo where Usuario_id=?;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			int nAmigos = 0;
			while (rs.next()) {
				nAmigos++;
			}
			movil.setnAmigos(nAmigos);

			sql = "SELECT * FROM Estado INNER JOIN Amigo ON Estado.autor = Amigo.amigo WHERE Amigo.Usuario_id=? LIMIT 10;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			Estado estadoaux = null;
			ArrayList<Estado> estados = new ArrayList<Estado>();
			try {
				while (rs.next()) {
					estadoaux = new Estado();
					estadoaux.setContenido(rs.getString("contenido"));
					estadoaux.setAutor(rs.getString("autor"));
					estadoaux.setFecha(rs.getDate("fecha"));
					estadoaux.setId(rs.getInt("id"));
					estadoaux.setUsuario_id(rs.getInt("Usuario_id"));
					estados.add(estadoaux);
				}
				movil.setEstadosAmigos(new ListaEstados(estados));
				conn.close();
				return Response.ok(movil).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.NOT_FOUND).entity("Error, no se encontro a: " + id).build();
			}

		} catch (NamingException | SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	public boolean usuarioExiste(String id) {
		try {
			String sql = "SELECT count(*) FROM myDeposdb.Usuario WHERE id=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("count(*)") > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			return false;
		}

	}
}
