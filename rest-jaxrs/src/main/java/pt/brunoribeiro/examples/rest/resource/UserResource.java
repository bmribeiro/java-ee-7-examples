package pt.brunoribeiro.examples.rest.resource;


import pt.brunoribeiro.examples.rest.model.User;
import pt.brunoribeiro.examples.rest.service.UserService;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;


    @GET
    public Response getUsers() {

        List<User> users = userService.findAll();

        JsonArrayBuilder array = Json.createArrayBuilder();

        for (User user : users) {
            array.add(toJson(user));
        }

        return Response.ok(array.build().toString()).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {

        User user = userService.findById(id);

        if (user == null) {
            return userNotFound();
        }

        return Response.ok(toJson(user).toString()).build();
    }

    @POST
    public Response createUser(
            String requestBody,
            @Context UriInfo uriInfo) {

        JsonObject json = readJson(requestBody);

        String name = json.getString("name", null);
        String email = json.getString("email", null);

        if (name == null || email == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Name and email are required\"}")
                    .build();
        }

        User user = userService.create(name, email);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(user.getId().toString())
                .build();

        return Response.created(location)
                .entity(toJson(user).toString())
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(
            @PathParam("id") Long id,
            String requestBody) {

        JsonObject json = readJson(requestBody);

        String name = json.getString("name", null);
        String email = json.getString("email", null);

        if (name == null || email == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Name and email are required\"}")
                    .build();
        }

        User user = userService.update(id, name, email);

        if (user == null) {
            return userNotFound();
        }

        return Response.ok(toJson(user).toString()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {

        boolean deleted = userService.delete(id);

        if (!deleted) {
            return userNotFound();
        }

        return Response.noContent().build();
    }

    private JsonObject readJson(String requestBody) {

        try (JsonReader reader =
                     Json.createReader(new StringReader(requestBody))) {

            return reader.readObject();
        }
    }

    private JsonObject toJson(User user) {

        return Json.createObjectBuilder()
                .add("id", user.getId())
                .add("name", user.getName())
                .add("email", user.getEmail())
                .build();
    }

    private Response userNotFound() {

        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"User not found\"}")
                .build();
    }
}