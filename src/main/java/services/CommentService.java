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

    /**
     * Retrieves all the comments from the system
     * @return all comments as a formatted string
     */
    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllComments() {
        return "--- Comment List ---\n"
                + commentList.stream()
                .map(Comment::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Retrieve specific comment from the system
     * @param id - id of the comment to be retrieved
     * @return requested comment as a String or an error message
     */
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

    /**
     * Retrieve all the replies(including nested) for specified comment
     * @param id - id of the comment to get replies to
     * @return all the replies to the comment or error message
     */
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

    /**
     * Recursive method to retrieve threaded replies for a comment
     * @param replies list of replies to particular comment
     * @param sb string builder instance with all comments
     */
    private void retrieveReplies(ArrayList<Comment> replies, StringBuilder sb) {
        for (int i = 0; i < replies.size(); i++) {
            sb.append(replies.get(i).toString()).append("\n");
            if (replies.get(i).getReplies().size() != 0)
                retrieveReplies(replies.get(i).getReplies(), sb);
        }
    }

    /**
     * Upvote specified comment
     * @param id - id of comment to upvote
     * @return 200 if success or 404 if not
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
     *  Downvote specified comment
     * @param id - id of comment to downvote
     * @return 200 if success or 404 if not
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

    /**
     * Add new comment to specified photo by specified user. Also creates
     * notification for owner of the photo if not same with user that posts
     * @param pId - id of photo to comment
     * @param uId - id of user that comments
     * @param is - input stream to read server response
     * @return 201 if success, 404 if user not found, 400 if photo not found
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("/{pId}/addComment/{uId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewComment(@PathParam("pId") long pId, @PathParam("uId") long uId, InputStream is) throws UnsupportedEncodingException {
        Optional<Photo> photoMatch = photoList.stream()
                .filter(p -> p.getId() == pId)
                .findFirst();
        if (photoMatch.isPresent()) {
            Optional<User> userMatch = userList.stream()
                    .filter(u -> u.getId() == uId)
                    .findFirst();

            if (userMatch.isPresent()) {
                Comment comment;
                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
                long commentId = Comment.getCounter().get();
                comment = gson.fromJson(json, Comment.class);

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
            return Response.status(Response.Status.BAD_REQUEST).build();

        return Response.status(201).build();
    }

    /**
     * Post a reply to specified comment under specified photo by specified user.
     * Creates notifications (if applicable) for owner of the photo and owner of the original comment that is
     * replied to.
     * @param pId id of the photo to reply under
     * @param uId id of the user that replies
     * @param cId id of the comment that is replied to
     * @param is input stream to read the data
     * @return 201 if success, 404 if any id is invalid
     * @throws UnsupportedEncodingException
     */
    @POST
    @Path("/{pId}/reply/{uId}/{cId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReplyToComment(@PathParam("pId") long pId, @PathParam("uId") long uId,
                                      @PathParam("cId") long cId, InputStream is) throws UnsupportedEncodingException {
        Optional<Photo> photoMatch = photoList.stream()
                .filter(p -> p.getId() == pId)
                .findFirst();
        if (photoMatch.isPresent()) {
            Optional<User> userMatch = userList.stream()
                    .filter(u -> u.getId() == uId)
                    .findFirst();

            if (userMatch.isPresent()) {
                Optional<Comment> commentMatch = commentList.stream()
                        .filter(c -> c.getId() == cId)
                        .findFirst();
                if (commentMatch.isPresent()) {
                    Comment reply;
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(new InputStreamReader(is, "UTF-8"));
                    long commentId = Comment.getCounter().get();
                    reply = gson.fromJson(json, Comment.class);

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
                        Notification nfReply = new Notification.NotificationBuilder()
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

    /**
     * Replaces the specified comment by a note "removed by admin" if the removing user is admin
     * @param uId id of user that is trying to remove
     * @param cId id of comment to be removed
     * @return 200 if success, 404 if ids are invalid, 403 if user is not admin
     */
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
