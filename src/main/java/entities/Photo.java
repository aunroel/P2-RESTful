package entities;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Photo {

    private final long id;
    private final User author;
    private final String name;
    private final ArrayList<Comment> comments;

    private static final AtomicLong counter = new AtomicLong(200);

    public Photo(PhotoBuilder pb) {
        this.id = pb.id;
        this.author = pb.author;
        this.name = pb.name;
        this.comments = pb.comments;
    }

    public Photo(User author, String name) {
        Photo photo =new Photo.PhotoBuilder().id()
                .author(author)
                .name(name)
                .build();
        this.id = photo.getId();
        this.author = photo.getAuthor();
        this.name = photo.getName();
        this.comments = photo.getComments();
    }

    public long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public static AtomicLong getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "Photo ID: " + id
                + "\nAuthor: " + author.getUserName()
                + "\nname: " + name
                + "\ncomments amount: " + getCommentsAmount()
                + "\n";
    }

    private int getCommentsAmount() {
        int amount = comments.size();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getReplies().size() != 0) {
                amount += comments.get(i).getReplies().size();
            }
        }

        return amount;
    }

    public static class PhotoBuilder {
        private long id;
        private User author;
        private String name;
        private ArrayList<Comment> comments = new ArrayList<>();

        public PhotoBuilder id() {
            this.id = Photo.counter.getAndIncrement();
            return this;
        }

        public PhotoBuilder id(long id) {
            this.id = id;
            return this;
        }

        public PhotoBuilder author(User author) {
            this.author = author;
            return this;
        }

        public PhotoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PhotoBuilder comments(Comment comment) {
            if (comment == null)
                return this;

            this.comments.add(comment);
            return this;
        }

        public Photo build() {
            return new Photo(this);
        }
    }
}
