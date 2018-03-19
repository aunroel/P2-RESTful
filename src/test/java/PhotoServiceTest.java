import com.google.gson.Gson;
import entities.Photo;
import entities.PseudoDB;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import services.PhotoService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhotoServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(PhotoService.class);
    }


    @Test
    public void photosAmountTest() {
        String response = target("photos/all").request().get(String.class);
        String[] uniquePhotos = response.split("Photo ID:");
        // original amount of photos = 4
        assertEquals(PseudoDB.getPhotos().size(), uniquePhotos.length - 1);
    }

    @Test
    public void getParticularPhotoTest() {
        String response = target("photos/all/200").request().get(String.class);
        assertTrue(response.contains("Photo ID: 200\nAuthor: nicky94"));
    }

    @Test
    public void getNonExistingPhotoTest() {
        String response = target("photos/all/210").request().get(String.class);
        assertEquals("Photo not found", response);
    }

    @Test
    public void getPhotoCommentsTest() {
        String response = target("photos/all/200/comments").request().get(String.class);
        String[] uniqueComments = response.split("Comment ID:");
        assertEquals(5, uniqueComments.length - 1);
    }

    @Test
    public void addPhotoTest() {
        Gson gson = new Gson();
        Photo photo = new Photo.PhotoBuilder()
                .name("test photo add")
                .build();

        String json = gson.toJson(photo, Photo.class);

        Response response = target("photos/addPhoto/3")
                .request()
                .post(Entity.json(json));

        assertEquals("Should return status 201", 201, response.getStatus());
    }

    @Test
    public void addPhotoNonExistingUserTest() {
        Gson gson = new Gson();
        Photo photo = new Photo.PhotoBuilder()
                .name("test photo add")
                .build();

        String json = gson.toJson(photo, Photo.class);

        Response response = target("photos/addPhoto/8")
                .request()
                .post(Entity.json(json));

        assertEquals("Should return status 404", 404, response.getStatus());
    }
}
