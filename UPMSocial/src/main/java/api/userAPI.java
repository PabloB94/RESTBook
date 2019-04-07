package api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users")
public class userAPI {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("name") String name){

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(){

    }

    @Path("{userid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(){

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editUser(){

    }

    @DELETE
    public Response deleteUser(){

    }

    @Path("{userid}/messages/page")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosts(@QueryParam("date") String name,
                             @QueryParam("start") String start,
                             @QueryParam("end") String end){

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishPost(){

    }

    @Path("{userid}/messages/personal")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages(){

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(){

    }

    @Path("{userid}/messages/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editMessage(){

    }

    @DELETE
    public Response deleteMessage(){

    }

    @Path("{userid}/friends")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFriend(){

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@QueryParam("name") String name,
                               @QueryParam("start") String start,
                               @QueryParam("end") String end){

    }

    @DELETE
    public Response deleteFriend(@QueryParam("idFriend") String idFriend){

    }

    @Path("{userid}/friends/updates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUpdates(@QueryParam("content") String content){

    }

    @Path("{userid}/mobile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMobile(@QueryParam("content") String content){

    }
}
