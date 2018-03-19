package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import entities.Notification;
import entities.PseudoDB;
import entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Path("/users")
public class UserService {

    private final CopyOnWriteArrayList<User> userList = PseudoDB.getUsers();
    private final CopyOnWriteArrayList<Notification> notifList = PseudoDB.getNotifications();

    private final Gson gson = new Gson();

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUsers() {
        return "--- User list ---\n"
                + userList.stream()
                .map(User::toString)
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/all/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@PathParam("id") long id) {
        Optional<User> match = userList.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return "--- User ---\n" + match.get().toString();
        } else {
            return "User not found";
        }
    }

    @GET
    @Path("/all/{id}/comments")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserComments(@PathParam("id") long id) {
        Optional<User> match = userList.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            StringBuilder sb = new StringBuilder();
            sb.append("--- User comments ---\n");
            for (int i = 0; i < match.get().getComments().size(); i++) {
                sb.append(match.get().getComments().get(i).toString()).append("\n");
            }
            return sb.toString();
        } else {
            return "User not found";
        }
    }

    @GET
    @Path("/all/{id}/notifications")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserNotif(@PathParam("id") long id) {
        Optional<User> match = userList.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            StringBuilder sb = new StringBuilder();
            sb.append("--- User notifications ---\n");
            if (match.get().getNotifications().size() == 0) {
                return sb.append("No notifications").append("\n").toString();
            }
            for (int i = 0; i < match.get().getNotifications().size(); i++) {
                match.get().getNotifications().get(i).getComment().setRead(true);
                sb.append(match.get().getNotifications().get(i).toString()).append("\n");
                notifList.remove(match.get().getNotifications().get(i));
                match.get().getNotifications().remove(match.get().getNotifications().get(i));

            }
            return sb.toString();
        } else {
            return "User not found";
        }
    }

    @POST
    @Path("/addUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(InputStream is) throws UnsupportedEncodingException {
        User user;
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
        long id = User.getCounter().get();
        user = gson.fromJson(json, User.class);

        user = new User.UserBuilder()
                .id(id)
                .userName(user.getUserName())
                .passwordSha256Hex(user.getSha256hexPassword())
                .isAdmin(user.isAdmin())
                .build();

        userList.add(user);
        return Response.status(201).build();
    }

    @DELETE
    @Path("/remove/{id}")
    public Response deleteUser(@PathParam("id") long id) {
        Predicate<User> user = u ->u.getId() == id;
        if (!userList.removeIf(user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else
            return Response.status(Response.Status.OK).build();
    }
}
