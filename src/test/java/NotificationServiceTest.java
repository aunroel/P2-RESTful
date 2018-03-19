import entities.PseudoDB;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import services.NotificationService;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotificationServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(NotificationService.class);
    }

    @Test
    public void notificationsAmountTest() {
        String response = target("notifications/all").request().get(String.class);
        String[] uniqueNotif = response.split("Notification ID:");
        assertEquals(PseudoDB.getNotifications().size(), uniqueNotif.length - 1);
    }

    @Test
    public void specificNotificationsTest() {
        String response = target("notifications/all/300").request().get(String.class);
        assertTrue(response.contains("owner: Sam\n"));
        assertTrue(response.contains("comment from: nicky94\n"));
        assertTrue(response.contains("comment body: yeah, got it couple weeks ago as a present!\n"));
    }

    @Test
    public void noNotificationTest() {
        String response = target("notifications/all/684").request().get(String.class);
        assertEquals("Notification not found", response);
    }



}
