package services;

import entities.Comment;
import entities.Notification;
import entities.PseudoDB;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/notifications")
public class NotificationService {

    private final CopyOnWriteArrayList<Notification> notificationList = PseudoDB.getNotifications();


    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllComments() {
        return "--- Notification List ---\n"
                + notificationList.stream()
                .map(Notification::toString)
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/all/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@PathParam("id") long id) {
        Optional<Notification> match = notificationList.stream()
                .filter(n -> n.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return "--- Notification ---\n" + match.get().toString();
        } else {
            return "Notification not found";
        }
    }
}
