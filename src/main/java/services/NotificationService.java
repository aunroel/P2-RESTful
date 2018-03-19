package services;

import entities.Comment;
import entities.Notification;
import entities.PseudoDB;
import entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public String getSpecificNotif (@PathParam("id") long id) {
        Optional<Notification> match = notificationList.stream()
                .filter(n -> n.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return  "--- Notification ---\n" + match.get().toString();
        } else {
            return "Notification not found";
        }
    }

}
