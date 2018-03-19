package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import entities.Comment;
import entities.Photo;
import entities.PseudoDB;
import entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/photos")
public class PhotoService {

    private final CopyOnWriteArrayList<Photo> photoList = PseudoDB.getPhotos();
    private final Gson gson = new Gson();

    /**
     * Get all photos from the server as formatted string
     * @return all photos as a string
     */
    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllPhotos() {
        return "--- Photo List ---\n"
                + photoList.stream()
                .map(Photo::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Get particular photo specified by an id
     * @param id id of the photo
     * @return photo with its details or an error if id is invalid
     */
    @GET
    @Path("/all/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@PathParam("id") long id) {
        Optional<Photo> match = photoList.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return "--- Photo ---\n" + match.get().toString();
        } else {
            return "Photo not found";
        }
    }

    /**
     * Get all the comments (including threaded) of specified photo
     * @param id id of photo to retrieve comments from
     * @return all comments for specified photo or an error
     */
    @GET
    @Path("/all/{id}/comments")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPhotoComments(@PathParam("id") long id) {
        Optional<Photo> match = photoList.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            Photo photo = match.get();
            StringBuilder sb = new StringBuilder();
            sb.append("User comments to photo ID " + id +"\n");
            for (int i = 0; i < photo.getComments().size(); i++) {
                sb.append(photo.getComments().get(i).toString()).append("\n");
                if (photo.getComments().get(i).getReplies().size() != 0) {
                    retrieveReplies(photo.getComments().get(i).getReplies(), sb);
                }
            }
            return sb.toString();
        } else {
            return "Photo not found";
        }
    }

    /**
     * Recursive method to help retrieve threaded comments (replies)
     * @param replies list of replies to some comment
     * @param sb sb that holds all processed comments
     */
    private void retrieveReplies(ArrayList<Comment> replies, StringBuilder sb) {
        for (int i = 0; i < replies.size(); i++) {
            sb.append(replies.get(i).toString()).append("\n");
            if (replies.get(i).getReplies().size() != 0)
                retrieveReplies(replies.get(i).getReplies(), sb);
        }
    }


    /**
     * Add photo from specified user to the system
     * @param id id of users that posts a new photo
     * @param is input stream to read the input
     * @return 201 if success, 404 if id is invalid
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("/addPhoto/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPhoto(@PathParam("id") long id, InputStream is) throws UnsupportedEncodingException {
        Optional<User> match = PseudoDB.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            Photo photo;
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
            long photoId = Photo.getCounter().get();
            photo = gson.fromJson(json, Photo.class);

            photo = new Photo.PhotoBuilder()
                    .id(photoId)
                    .author(match.get())
                    .name(photo.getName())
                    .comments(null)
                    .build();

            photoList.add(photo);
            match.get().getPhotos().add(photo);

            return Response.status(201).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
