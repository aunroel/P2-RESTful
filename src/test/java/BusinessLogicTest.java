import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import services.UserService;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BusinessLogicTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(UserService.class);
    }

    @Test
    public void usersAmountTest() {
        String response = target("users/all").request().get(String.class);
        String[] uniqueUsers = response.split("ID:");
        int redundantInfoAmount = 0;

        for (int i = 0; i < uniqueUsers.length; i++) {
           if (uniqueUsers[i].contains("User list")) {
               redundantInfoAmount++;
           }
        }

        assertEquals(4, uniqueUsers.length - redundantInfoAmount);
    }

    @Test
    public void getParticularUserTest() {
        String response = target("users/all/4").request().get(String.class);
        assertTrue(response.contains("ID: 4\nnickname: Sam"));
    }

    @Test
    public void getNonExistingUserTest() {
        String response = target("users/all/5").request().get(String.class);
        assertEquals("User not found", response);
    }

    @Test
    public void getAllUserCommentsTest() {
        String response = target("users/all/3/comments").request().get(String.class);
        assertTrue(response.contains("wait, was it your birthday not long ago?"));
    }


}
