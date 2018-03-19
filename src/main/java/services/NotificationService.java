package services;

import entities.Notification;
import entities.PseudoDB;

import javax.ws.rs.*;
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
    public String getSpecificNotif (@PathParam("id") long id) {
        Optional<Notification> match = notificationList.stream()
                .filter(n -> n.getId() == id)
                .findFirst();
        return match.map(notification -> "--- Notification ---\n" + notification.toString()).orElse("Notification not found");
    }

}
