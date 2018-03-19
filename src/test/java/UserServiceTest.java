import com.google.gson.Gson;
import entities.PseudoDB;
import entities.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import services.UserService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(UserService.class);
    }

    @Test
    public void usersAmountTest() {
        String response = target("users/all").request().get(String.class);

        // first element of the array is the phrase "Users list", so we don't count it
        String[] uniqueUsers = response.split("ID:");

        // need to use user list because the test fails if all tests are run simultaneously
        // (probably cause of addition of new user in later tests)
        // original users size is 4
        assertEquals(PseudoDB.getUsers().size(), uniqueUsers.length - 1);

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
    public void getAllUserCommentsTest1() {
        String response = target("users/all/3/comments").request().get(String.class);
        // user with id 3 has only 1 comment
        assertTrue(response.contains("wait, was it your birthday not long ago?"));
    }

    @Test
    public void getAllUserCommentsTest2() {
        String response = target("users/all/1/comments").request().get(String.class);
        // user with id 3 has only 1 comment
        assertTrue(response.contains("yeah, got it couple weeks ago as a present!"));
        assertTrue(response.contains("awesome views!"));
        assertTrue(response.contains("yes! and someone forgot about it -_-"));
    }

    @Test
    public void getAllUserNotifTest() {
        String response = target("users/all/4/notifications").request().get(String.class);
        assertTrue(response.contains("ID: 300"));
        assertTrue(response.contains("owner: Sam"));
        assertTrue(response.contains("yeah, got it couple weeks ago as a present!"));
        String response2 = target("users/all/4/notifications").request().get(String.class);
        assertEquals("--- User notifications ---\nNo notifications\n", response2);
    }

    @Test
    public void addUserTest() {
        Gson gson = new Gson();
        User user = new User.UserBuilder()
                .id()
                .userName("Jimmy")
                .passwordSha256Hex("what")
                .isAdmin(false)
                .build();

        String json = gson.toJson(user, User.class);

        Response response = target("users/addUser")
              .request()
              .post(Entity.json(json));

        assertEquals("Should return status 201", 201, response.getStatus());
    }

    @Test
    public void deleteInvalidUserTest() {
        Response response = target("users/remove/5")
                .request()
                .delete();

        assertEquals("Should return status 400", 400, response.getStatus());
    }

    @Test
    public void deleteUserTest() {
        Response response = target("users/remove/4")
                .request()
                .delete();

        assertEquals("Should return status 200", 200, response.getStatus());
    }


}
