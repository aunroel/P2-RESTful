
import com.google.gson.Gson;
import entities.Comment;
import entities.PseudoDB;
import entities.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import services.CommentService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;


import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommentServiceTest extends JerseyTest{

    @Override
    protected Application configure() {
        return new ResourceConfig(CommentService.class);
    }

    @Test
    public void commentsAmountTest() {
        String response = target("comments/all").request().get(String.class);
        String[] uniqueUsers = response.split("Comment ID:");

        // originally was 7 comments
        assertEquals(PseudoDB.getComments().size(), uniqueUsers.length - 1);

    }

    @Test
    public void getParticularCommentTest() {
        String response = target("comments/all/102").request().get(String.class);
        assertTrue(response.contains("Author: Sam\n"));
        assertTrue(response.contains("body: \"nice, it's really cute!\"\n"));
    }

    @Test
    public void getInvalidComment() {
        String response = target("comments/all/654").request().get(String.class);
        assertEquals("Comment not found", response);
    }

    @Test
    public void getRepliesToComment() {
        String response = target("comments/100/replies").request().get(String.class);
        String[] responseAsArray = response.split("~~~~~~~~~~~~~~~~~~\n");
        String[] replies = responseAsArray[1].split("Comment ID:");
        assertEquals(2, replies.length - 1);
        assertTrue(replies[1].contains("yeah, got it couple weeks ago as a present!"));
        assertTrue(replies[2].contains("nice, it's really cute!"));
    }

    @Test
    public void upVoteTest() {
        Response response = target("comments/102/upvote")
                .request()
                .put(Entity.text(""));

        Optional<Comment> match = PseudoDB.getComments().stream()
                .filter(c -> c.getId() == 102)
                .findFirst();
        if (match.isPresent()) {
            assertEquals(2, match.get().getUpVotes());
        } else
            assertEquals(1, match.get().getUpVotes());

        assertEquals("Should return status 200", 200, response.getStatus());
    }

    @Test
    public void downVoteTest() {
        Response response = target("comments/103/downvote")
                .request()
                .put(Entity.text(""));

        Optional<Comment> match = PseudoDB.getComments().stream()
                .filter(c -> c.getId() == 103)
                .findFirst();
        if (match.isPresent()) {
            assertEquals(2, match.get().getDownVotes());
        } else
            assertEquals(1, match.get().getDownVotes());

        assertEquals("Should return status 200", 200, response.getStatus());
    }

    @Test
    public void addCommentToPhotoTest() {
        Gson gson = new Gson();
        Comment comment = new Comment.CommentBuilder()
                .id()
                .author(new User())
                .timeStamp()
                .body("test add comment")
                .reply(null)
                .read(false)
                .build();

        String json = gson.toJson(comment, Comment.class);

        // user with id 2 is the one posting a comment to photo with id 203
        Response response = target("comments/201/addComment/2")
                .request()
                .post(Entity.json(json));

        assertEquals("Should return status 201", 201, response.getStatus());
    }

    @Test
    public void addReplyToCommentTest() {
        Gson gson = new Gson();
        Comment comment = new Comment.CommentBuilder()
                .id()
                .author(new User())
                .timeStamp()
                .body("test add reply")
                .reply(null)
                .read(false)
                .build();

        String json = gson.toJson(comment, Comment.class);

        // user with id 2 is the one posting a reply to comment with id 106 on photo with id 201
        Response response = target("comments/201/reply/2/106")
                .request()
                .post(Entity.json(json));

        assertEquals("Should return status 201", 201, response.getStatus());
    }


    @Test
    public void addNoSuchUserCommentTest() {
        Gson gson = new Gson();
        Comment comment = new Comment.CommentBuilder()
                .id()
                .author(new User())
                .timeStamp()
                .body("test add comment")
                .reply(null)
                .read(false)
                .build();

        String json = gson.toJson(comment, Comment.class);

        Response response = target("comments/203/addComment")
                .request()
                .post(Entity.json(json));

        assertEquals("Should return status 404", 404, response.getStatus());
    }

    @Test
    public void addNoSuchPhotoCommentTest() {
        Response response = target("comments/101/addComment/2")
                .request()
                .post(Entity.json(""));

        assertEquals("Should return status 400", 400, response.getStatus());
    }

    @Test
    public void removeCommentNotAdminTest() {
        Response response = target("comments/remove/2/107")
                .request()
                .put(Entity.text(""));
        assertEquals("Should return status 403", 403, response.getStatus());

    }

    @Test
    public void removeCommentNoUserTest() {
        Response response = target("comments/remove/2411/107")
                .request()
                .put(Entity.text(""));
        assertEquals("Should return status 404", 404, response.getStatus());
    }

    @Test
    public void removeCommentAdminTest() {
        Response response = target("comments/remove/3/106")
                .request()
                .put(Entity.text(""));
        assertEquals("Should return status 200", 200, response.getStatus());

    }

}
