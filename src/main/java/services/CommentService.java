package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import entities.*;

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

@Path("/comments")
public class CommentService {

    private final CopyOnWriteArrayList<Comment> commentList = PseudoDB.getComments();
    private final CopyOnWriteArrayList<User> userList = PseudoDB.getUsers();
    private final CopyOnWriteArrayList<Photo> photoList = PseudoDB.getPhotos();
    private final CopyOnWriteArrayList<Notification> notificationList = PseudoDB.getNotifications();

    private Gson gson = new Gson();

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllComments() {
        return "--- Comment List ---\n"
                + commentList.stream()
                .map(Comment::toString)
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/all/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@PathParam("id") long id) {
        Optional<Comment> match = commentList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return "--- Comment ---\n" + match.get().toString();
        } else {
            return "Comment not found";
        }
    }

    @GET
    @Path("/{id}/replies")
    @Produces(MediaType.TEXT_PLAIN)
    public String getReplies(@PathParam("id") long id) {
        Optional<Comment> match = commentList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            Comment comment = match.get();

            StringBuilder sb = new StringBuilder();
            sb.append("Original comment with id " + id + ":\n");
            sb.append(comment.toString());
            sb.append("~~~~~~~~~~~~~~~~~~\n");
            sb.append("Replies to comment ID " + id + ":\n");
            if (comment.getReplies().size() == 0) {
                sb.append("No replies found");
            }
            for (int i = 0; i < comment.getReplies().size(); i++) {
                sb.append(comment.getReplies().get(i).toString()).append("\n");
                if (comment.getReplies().get(i).getReplies().size() != 0) {
                    retrieveReplies(comment.getReplies().get(i).getReplies(), sb);
                }
            }
            return sb.toString();
        } else {
            return "Comment not found";
        }
    }

    private void retrieveReplies(ArrayList<Comment> replies, StringBuilder sb) {
        for (int i = 0; i < replies.size(); i++) {
            sb.append(replies.get(i).toString()).append("\n");
            if (replies.get(i).getReplies().size() != 0)
                retrieveReplies(replies.get(i).getReplies(), sb);
        }
    }

    /**
     * Done
     * @param id
     * @return
     */
    @PUT
    @Path("/{id}/upvote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response upVoteComment(@PathParam("id") long id) {
        Optional<Comment> match = commentList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            match.get().upVote();
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     *  Done
     * @param id
     * @return
     */
    @PUT
    @Path("/{id}/downvote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downVoteComment(@PathParam("id") long id) {
        Optional<Comment> match = commentList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            match.get().downVote();
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{pId}/addComment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewComment(@PathParam("pId") long pId, InputStream is) throws UnsupportedEncodingException {
        Optional<Photo> photoMatch = photoList.stream()
                .filter(p -> p.getId() == pId)
                .findFirst();
        if (photoMatch.isPresent()) {
            Comment comment;
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
            long commentId = Comment.getCounter().get();
            comment = gson.fromJson(json, Comment.class);

            Comment finalComment = comment;
            Optional<User> userMatch = userList.stream()
                    .filter(u -> u.getId() == finalComment.getAuthor().getId())
                    .findFirst();

            if (userMatch.isPresent()) {
                comment = new Comment.CommentBuilder()
                        .id(commentId)
                        .author(userMatch.get())
                        .timeStamp()
                        .body(comment.getBody())
                        .reply(null)
                        .read(false)
                        .build();

                userMatch.get().getComments().add(comment);
                photoMatch.get().getComments().add(comment);
                commentList.add(comment);

                if (photoMatch.get().getAuthor().getId() != userMatch.get().getId()) {
                    Notification nf = new Notification.NotificationBuilder()
                            .id()
                            .comment(comment)
                            .isReply(false)
                            .owner(photoMatch.get().getAuthor())
                            .build();
                    photoMatch.get().getAuthor().getNotifications().add(nf);
                    notificationList.add(nf);
                }

            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.status(201).build();
    }

    @POST
    @Path("/{pId}/reply/{cId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReplyToComment(@PathParam("pId") long pId, @PathParam("cId") long cId, InputStream is) throws UnsupportedEncodingException {
        Optional<Photo> photoMatch = photoList.stream()
                .filter(p -> p.getId() == pId)
                .findFirst();
        if (photoMatch.isPresent()) {
            Optional<Comment> commentMatch = commentList.stream()
                    .filter(c -> c.getId() == cId)
                    .findFirst();
            if (commentMatch.isPresent()) {
                Comment reply;
                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
                long commentId = Comment.getCounter().get();
                reply = gson.fromJson(json, Comment.class);

                Comment finalComment = reply;
                Optional<User> userMatch = userList.stream()
                        .filter(u -> u.getId() == finalComment.getAuthor().getId())
                        .findFirst();

                if (userMatch.isPresent()) {
                    reply = new Comment.CommentBuilder()
                            .id(commentId)
                            .author(userMatch.get())
                            .timeStamp()
                            .body(reply.getBody())
                            .reply(null)
                            .read(false)
                            .build();

                    commentMatch.get().getReplies().add(reply);
                    userMatch.get().getComments().add(reply);
                    commentList.add(reply);


                    if (photoMatch.get().getAuthor().getId() != userMatch.get().getId()) {
                        Notification nfComment = new Notification.NotificationBuilder()
                                .id()
                                .comment(reply)
                                .isReply(false)
                                .owner(photoMatch.get().getAuthor())
                                .build();

                        photoMatch.get().getAuthor().getNotifications().add(nfComment);
                        notificationList.add(nfComment);
                    }
                    if (reply.getAuthor().getId() != commentMatch.get().getAuthor().getId()) {
                        Notification nfReply =  new Notification.NotificationBuilder()
                                .id()
                                .comment(reply)
                                .isReply(true)
                                .owner(commentMatch.get().getAuthor())
                                .build();

                        commentMatch.get().getAuthor().getNotifications().add(nfReply);
                        notificationList.add(nfReply);
                    }

                } else
                    return Response.status(Response.Status.NOT_FOUND).build();
            } else
                return Response.status(Response.Status.NOT_FOUND).build();
        } else
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.status(201).build();
    }

    @PUT
    @Path("/remove/{uId}/{cId}")
    public Response deleteComment(@PathParam("uId") long uId, @PathParam("cId") long cId) {
        Optional<User> userMatch = userList.stream()
                .filter(u -> u.getId() == uId)
                .findFirst();

        if (userMatch.isPresent()) {
            if (userMatch.get().isAdmin()) {
                Optional<Comment> commentMatch = commentList.stream()
                        .filter(c -> c.getId() == cId)
                        .findFirst();

                if (commentMatch.isPresent()) {
                    Comment toReplace = commentMatch.get();
                    Comment adminReplacement = new Comment.CommentBuilder()
                            .id(toReplace.getId())
                            .author(userMatch.get())
                            .body("Comment removed by admin")
                            .timeStamp()
                            .read(toReplace.isRead())
                            .replies(toReplace.getReplies())
                            .build();

                    toReplace.update(adminReplacement);

                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        return Response.status(Response.Status.OK).build();
    }

}
