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


    /**
     * Get all the notifications from the server regardless of their owners
     * @return string representation of all the notifications
     */
    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllComments() {
        return "--- Notification List ---\n"
                + notificationList.stream()
                .map(Notification::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Get specified notification from the server or an error if there is none with such an id
     * @param id id of the notification to be retrieved
     * @return specified notification or an error message
     */
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
