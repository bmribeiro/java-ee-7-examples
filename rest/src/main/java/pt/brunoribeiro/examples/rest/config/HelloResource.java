package pt.brunoribeiro.examples.rest.config;

import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello Java EE 7 REST!";
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloName(@PathParam("name") String name) {
        return "Hello " + name + "!";
    }

    @GET
    @Path("/json/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String helloJson(@PathParam("name") String name) {

        return Json.createObjectBuilder()
                .add("message", "Hello " + name + "!")
                .build()
                .toString();
    }
}