package services;

import com.google.gson.Gson;
import entities.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/comments")
public class CommentService {

    private final CopyOnWriteArrayList<Comment> commentList = PseudoDB.getComments();
    private final CopyOnWriteArrayList<User> userList = PseudoDB.getUsers();
    private final CopyOnWriteArrayList<Photo> photoList = PseudoDB.getPhotos();
    private final CopyOnWriteArrayList<Notification> notificationList = PseudoDB.getNotifications();

    private Gson gson = new Gson();

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllComments() {
        return "--- Comment List ---\n"
                + commentList.stream()
                .map(Comment::toString)
                .collect(Collectors.joining("\n"));
    }
}
