package services;

import com.google.gson.Gson;
import entities.PseudoDB;
import entities.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/users")
public class UserService {

    private final CopyOnWriteArrayList<User> userList = PseudoDB.getUsers();

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUsers() {
        return "--- User list ---\n"
                + userList.stream()
                .map(User::toString)
                .collect(Collectors.joining("\n"));

    }
}
