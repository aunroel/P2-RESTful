package entities;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.digest.DigestUtils;

public class User {

    private final long id;
    private final String userName;
    private final String sha256hexPassword;
    private final boolean isAdmin;
    private final ArrayList<Comment> comments;
    private final ArrayList<Notification> notifications;
    private final ArrayList<Photo> photos;

    private static final AtomicLong counter = new AtomicLong(1);

    private User(UserBuilder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.sha256hexPassword = builder.sha256hexPassword;
        this.isAdmin = builder.isAdmin;
        this.comments = builder.comments;
        this.notifications = builder.notifications;
        this.photos = builder.photos;
    }

    public User() {
        User user = new UserBuilder().id().build();
        this.id = user.id;
        this.userName = user.userName;
        this.sha256hexPassword = user.sha256hexPassword;
        this.isAdmin = user.isAdmin;
        this.comments = user.comments;
        this.notifications = user.notifications;
        this.photos = user.photos;
    }

    public User(String userName, String password, boolean isAdmin, Comment comment,
                Notification notification, Photo photo) {
        User user = new UserBuilder()
                .id()
                .userName(userName)
                .passwordSha256Hex(password)
                .isAdmin(isAdmin)
                .comments(comment)
                .notifications(notification)
                .photos(photo)
                .build();
        this.id = user.getId();
        this.userName = user.getUserName();
        this.sha256hexPassword = user.getSha256hexPassword();
        this.isAdmin = user.isAdmin();
        this.comments = user.getComments();
        this.notifications = user.getNotifications();
        this.photos = user.getPhotos();
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getSha256hexPassword() {
        return sha256hexPassword;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public static AtomicLong getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + "\nnickname: " + userName
                + "\npassword: " + sha256hexPassword
                + "\nadmin: " + isAdmin
                + "\n# of comments: " + comments.size()
                + "\n# of notifications: " + notifications.size()
                + "\n# of photos: " + photos.size()
                + "\n";
    }

    public static class UserBuilder {
        private long id;
        private String userName = "";
        private String sha256hexPassword = "";
        private boolean isAdmin;
        private ArrayList<Comment> comments = new ArrayList<>();
        private ArrayList<Notification> notifications = new ArrayList<>();
        private ArrayList<Photo> photos = new ArrayList<>();


        public UserBuilder id() {
            this.id = User.counter.getAndIncrement();
            return this;
        }

        public UserBuilder id(long id) {
            this.id = id;
            return this;
        }

        public UserBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Encrypts user password into sha256 hex form and stores
         * it this way on the server.
         * Did not want to store and operate on original passwords
         * across the network
         * @param password
         * @return
         */
        public UserBuilder passwordSha256Hex(String password) {
            this.sha256hexPassword = DigestUtils.sha256Hex(password);
            return this;
        }

        public UserBuilder isAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public UserBuilder comments(Comment comment) {
            if (comment == null)
                return this;

            this.comments.add(comment);
            return this;
        }

        public UserBuilder notifications(Notification notification) {
            if (notification == null)
                return this;

            this.notifications.add(notification);
            return this;
        }

        public UserBuilder photos(Photo photo) {
            if (photo == null)
                return this;

            this.photos.add(photo);
            return this;
        }

        public User build() {
            return new User(this);
        }

    }
}
