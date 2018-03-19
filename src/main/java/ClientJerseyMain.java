import com.google.gson.Gson;
import entities.Comment;
import entities.Photo;
import entities.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Scanner;

public class ClientJerseyMain {

    static Gson gson = new Gson();
    static Client client = ClientBuilder.newClient();
    static WebTarget webTarget = client.target("http://localhost:8080/P2");
    static Scanner in = new Scanner(System.in, "UTF-8");

    public static void main(String[] args) {
        System.out.print(createMenu());

        int number = 0;
        while (number != -1) {
            number = in.nextInt();
            switch (number) {
                case 1:
                    getAllUsers();
                    break;
                case 2:
                    getSpecificUser();
                    break;
                case 3:
                    getCommentsOfSpecificUser();
                    break;
                case 4:
                    getAndReadAllUserNotifications();
                    break;
                case 5:
                    addNewUser();
                    break;
                case 6:
                    deleteUser();
                    break;
                case 7:
                    getAllPhotos();
                    break;
                case 8:
                    getSpecificPhoto();
                    break;
                case 9:
                    getAllCommentsOfSpecificPhoto();
                    break;
                case 10:
                    addNewPhoto();
                    break;
                case 11:
                    getAllNotifications();
                    break;
                case 12:
                    getSpecificNotification();
                    break;
                case 13:
                    getAllComments();
                    break;
                case 14:
                    getSpecificComment();
                    break;
                case 15:
                    getCommentReplies();
                    break;
                case 16:
                    addCommentToPhoto();
                    break;
                case 17:
                    addReplyToComment();
                    break;
                case 18:
                    upVoteComment();
                    break;
                case 19:
                    downVoteComment();
                    break;
                case 20:
                    removeComment();
                    break;
                case 21:
                    System.out.println(createMenu());
                    break;
                case -1:
                    System.out.println("Goodbye!");
                    System.exit(1);
            }
        }
    }

    /**
     * creates a menu with all possible actions for the client
     * @return menu with options to choose
     */
    private static String createMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enter listed below number to get:\n");
        sb.append("1: All users\n");
        sb.append("2: Specific user\n");
        sb.append("3: Get comments posted by specific user\n");
        sb.append("4: Get notifications (and read them) for specific user\n");
        sb.append("5: Add new user\n");
        sb.append("6: Delete specific user\n");
        sb.append("7: Get all photos\n");
        sb.append("8: Get specific photo\n");
        sb.append("9: Get all comments for specific photo\n");
        sb.append("10: Add new photo\n");
        sb.append("11: Get all notifications\n");
        sb.append("12: Get specific notification\n");
        sb.append("13: Get all comments\n");
        sb.append("14: Get specific comment\n");
        sb.append("15: Get replies (including nested) for specific comment\n");
        sb.append("16: Add new comment to specific photo\n");
        sb.append("17: Add reply to a specific comment\n");
        sb.append("18: Up vote specific comment\n");
        sb.append("19: Down vote specific comment\n");
        sb.append("20: Remove comment (only if user is admin)\n");
        sb.append("21: Draw the menu\n");
        sb.append("-1: Exit the programme\n");
        sb.append("Your choice: ");

        return sb.toString();
    }

    /**
     * Get all users from the server
     */
    private static void getAllUsers() {
        String response = webTarget.path("/users/all")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get specific user from the server
     */
    private static void getSpecificUser() {
        System.out.print("Enter id of the user: ");
        String response = webTarget.path("/users/all/" + in.nextInt())
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all the comments made by specific user from the server
     */
    private static void getCommentsOfSpecificUser() {
        System.out.print("Enter id of the user: ");
        String response = webTarget.path("/users/all/" + in.nextInt() + "/comments")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all notifications for specific users.
     * Marks all notifications as read in the process
     */
    private static void getAndReadAllUserNotifications() {
        System.out.print("Enter id of the user: ");
        String response = webTarget.path("/users/all/" + in.nextInt() + "/notifications")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Add a new user to the system
     */
    private static void addNewUser() {
        System.out.println("Enter name of the user: ");
        in.nextLine();
        String name = in.nextLine();
        System.out.println("Enter password: ");
        String password = in.nextLine();
        System.out.print("Is user an admin (y/n): ");
        boolean isAdmin = in.nextLine().equalsIgnoreCase("y");
        User newUser = new User.UserBuilder()
                .id()
                .userName(name)
                .passwordSha256Hex(password)
                .isAdmin(isAdmin)
                .build();
        Response response = webTarget.path("/users/addUser")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(gson.toJson(newUser)));
        if (response.getStatus() == 201) {
            System.out.println("New user created");
        } else {
            System.out.println("Failed to create a user");
        }
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Delete a user from the system
     */
    private static void deleteUser() {
        System.out.println("Enter id of user to be deleted:");
        Response response = webTarget.path("/users/remove/" + in.nextInt())
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();
        if (response.getStatus() == 200) {
            System.out.println("User deleted");
        } else {
            System.out.println("User not found");
        }
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all photos from the server
     */
    private static void getAllPhotos() {
        String response = webTarget.path("/photos/all")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get specific photo from the server
     */
    private static void getSpecificPhoto() {
        System.out.println("Enter photo id");
        String response = webTarget.path("/photos/all/" + in.nextInt())
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all the comments (including replies to them) for a specific photo
     */
    private static void getAllCommentsOfSpecificPhoto() {
        System.out.println("Enter photo id");
        String response = webTarget.path("/photos/all/" + in.nextInt() + "/comments")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Add new photo to the system for a user-specified user
     * Do nothing if there's no requested user
     */
    private static void addNewPhoto() {
        System.out.println("Enter user id to post a photo:");
        int id = in.nextInt();
        in.nextLine();
        System.out.println("Enter name of photo: ");
        String photoName = in.nextLine();

        Photo newPhoto = new Photo.PhotoBuilder()
                .author(new User())
                .name(photoName)
                .build();

        Response response = webTarget.path("/photos/addPhoto/" + id)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(gson.toJson(newPhoto)));

        if (response.getStatus() == 201) {
            System.out.println("New photo created");
        } else {
            System.out.println("Failed to post a new photo: " + response.getStatus());
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all notifications from the server
     */
    private static void getAllNotifications() {
        String response = webTarget.path("/notifications/all")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get specific notification from the server
     */
    private static void getSpecificNotification() {
        System.out.println("Enter notification id");
        String response = webTarget.path("/notifications/all/" + in.nextInt())
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.println(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get all the comments from the server
     */
    private static void getAllComments() {
        String response = webTarget.path("/comments/all")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.print(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get specific comment from the server
     */
    private static void getSpecificComment() {
        System.out.println("Enter comment id");
        String response = webTarget.path("/comments/all/" + in.nextInt())
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.println(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Get replies to the specific comment from the server.
     * Also retrieves nested (threaded) replies
     */
    private static void getCommentReplies() {
        System.out.println("Enter comment id");
        String response = webTarget.path("/comments/" + in.nextInt() + "/replies")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        System.out.println(response);
        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Add new comment to specified photo
     */
    private static void addCommentToPhoto() {
        System.out.println("Enter photo id to post a comment:");
        int photoId = in.nextInt();
        in.nextLine();

        System.out.println("Enter user id that will post a comment: ");
        int perId = in.nextInt();
        in.nextLine();

        System.out.println("Enter comment body:");
        String cBody = in.nextLine();

        Comment newComment = new Comment.CommentBuilder()
                .author(new User())
                .timeStamp()
                .body(cBody)
                .read(false)
                .build();

        Response response = webTarget.path("/comments/" + photoId + "/addComment/" + perId)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(gson.toJson(newComment)));

        if (response.getStatus() == 201) {
            System.out.println("New comment created");
        } else {
            System.out.println("Failed to post a new comment: " + response.getStatus());
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Add new reply to specified comment under specified photo
     */
    private static void addReplyToComment() {
        System.out.println("Enter photo id to post a reply:");
        int phId = in.nextInt();
        in.nextLine();
        System.out.println("Enter user id that will post a reply: ");
        int persId = in.nextInt();
        in.nextLine();

        System.out.println("Enter comment id that will be replied to: ");
        int commId = in.nextInt();
        in.nextLine();


        System.out.println("Enter reply body:");
        String comBody = in.nextLine();

        Comment nComment = new Comment.CommentBuilder()
                .author(new User())
                .timeStamp()
                .body(comBody)
                .read(false)
                .build();

        Response response = webTarget.path("/comments/" + phId + "/reply/" + persId + "/" + commId)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(gson.toJson(nComment)));

        if (response.getStatus() == 201) {
            System.out.println("New comment created");
        } else {
            System.out.println("Failed to post a new comment: " + response.getStatus());
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Upvote specified comment
     */
    private static void upVoteComment() {
        System.out.println("Enter id of comment to up vote");
        Response response = webTarget.path("/comments/" + in.nextInt() + "/upvote")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.text(""));
        if (response.getStatus() == 200) {
            System.out.println("Comment up voted");
        } else {
            System.out.println("Couldn't find comment with such id");
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Downvote specified comment
     */
    private static void downVoteComment() {
        System.out.println("Enter id of comment to down vote");
        Response response = webTarget.path("/comments/" + in.nextInt() + "/downvote")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.text(""));
        if (response.getStatus() == 200) {
            System.out.println("Comment down voted");
        } else {
            System.out.println("Couldn't find comment with such id");
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }

    /**
     * Remove specified comment if given user is an admin
     * Doesn't actually remove the comment, but changes the body of comment
     * to a note
     */
    private static void removeComment() {
        System.out.println("Enter id of user attempting to remove comment");
        int adminId = in.nextInt();
        System.out.println("Enter id of comment to be removed");
        int remCommId = in.nextInt();
        Response response = webTarget.path("/comments/remove/" + adminId + "/" + remCommId)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.text(""));
        if (response.getStatus() == 200) {
            System.out.println("Comment removed by admin");
        } else {
            System.out.println("Either no comment found, or user is not existing/not admin");
        }

        System.out.println("-------------------");
        System.out.println("Your choice: ");
    }


}
