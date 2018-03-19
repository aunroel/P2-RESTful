package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Comment {

    private long id;
    private transient User author;
    private String timeStamp;
    private String body;
    private ArrayList<Comment> replies;
    private int upVotes = 0;
    private int downVotes = 0;
    private boolean isRead;

    private static final AtomicLong counter = new AtomicLong(100);

    public Comment(CommentBuilder commentBuilder) {
        this.id = commentBuilder.id;
        this.author = commentBuilder.author;
        this.timeStamp = commentBuilder.timeStamp;
        this.body = commentBuilder.body;
        this.replies = commentBuilder.replies;
        this.upVotes = commentBuilder.upVotes;
        this.downVotes = commentBuilder.downVotes;
        this.isRead = commentBuilder.isRead;
    }

    public Comment() {
        Comment comment = new CommentBuilder().id().timeStamp().build();
        this.id = comment.id;
        this.author = comment.author;
        this.timeStamp = comment.timeStamp;
        this.body = comment.body;
        this.replies = comment.replies;
        this.upVotes = comment.upVotes;
        this.downVotes = comment.downVotes;
        this.isRead = comment.isRead;
    }

    public Comment(User author, String body) {
        Comment comment = new CommentBuilder().id()
                .timeStamp()
                .author(author)
                .body(body)
                .reply(null)
                .build();
        this.id = comment.getId();
        this.author = comment.getAuthor();
        this.timeStamp = comment.getTimeStamp();
        this.body = comment.getBody();
        this.replies = comment.getReplies();
        this.upVotes = comment.getUpVotes();
        this.downVotes = comment.getDownVotes();
        this.isRead = comment.isRead();
    }

    public long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getBody() {
        return body;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public boolean isRead() {
        return isRead;
    }

    public void upVote() {
        this.upVotes++;
    }

    public void downVote() {
        this.downVotes++;
    }

    public static AtomicLong getCounter() {
        return counter;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Comment update(Comment comment) {
        this.id = comment.getId();
        this.author = comment.getAuthor();
        this.timeStamp = comment.getTimeStamp();
        this.body = comment.getBody();
        this.replies = comment.getReplies();
        this.upVotes = 0;
        this.downVotes = 0;
        this.isRead = comment.isRead;

        return this;
    }

    @Override
    public String toString() {
        return "Comment ID: " + id
                + "\nAuthor: " + author.getUserName()
                + "\ntimestamp: " + timeStamp
                + "\nbody: \"" + body + "\""
                + "\nreplies: " + replies.size()
                + "\nup votes: " + upVotes
                + "\ndown votes: " + downVotes
                + "\nis read: " + isRead
                + "\n";
    }

    public static class CommentBuilder {
        private long id;
        private User author;
        private String timeStamp;
        private String body;
        private ArrayList<Comment> replies = new ArrayList<>();
        private int upVotes = 0;
        private int downVotes = 0;
        private boolean isRead;

        public CommentBuilder id() {
            this.id = Comment.counter.getAndIncrement();
            return this;
        }

        public CommentBuilder id(long id) {
            this.id = id;
            return this;
        }

        public CommentBuilder author(User author) {
            this.author = author;
            return this;
        }

        public CommentBuilder timeStamp() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.timeStamp = LocalDateTime.now().format(formatter);
            return this;
        }

        public CommentBuilder body(String body) {
            this.body = body;
            return this;
        }

        public CommentBuilder reply(Comment comment) {
            if (comment == null)
                return this;

            this.replies.add(comment);
            return this;
        }

        public CommentBuilder replies(ArrayList<Comment> comment) {
            if (comment == null)
                return this;

            this.replies.addAll(comment);
            return this;
        }

        public CommentBuilder upVote() {
            this.upVotes++;
            return this;
        }

        public CommentBuilder downVote() {
            this.downVotes++;
            return this;
        }

        public CommentBuilder read(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
