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

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllPhotos() {
        return "--- Photo List ---\n"
                + photoList.stream()
                .map(Photo::toString)
                .collect(Collectors.joining("\n"));
    }

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


    // TODO need to test it as well
    private void retrieveReplies(ArrayList<Comment> replies, StringBuilder sb) {
        for (int i = 0; i < replies.size(); i++) {
            sb.append(replies.get(i).toString()).append("\n");
            if (replies.get(i).getReplies().size() != 0)
                retrieveReplies(replies.get(i).getReplies(), sb);
        }
    }


    // TODO need to test this
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
