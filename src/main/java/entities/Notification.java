package entities;

import java.util.concurrent.atomic.AtomicLong;

public class Notification {

    private final long id;
    private final User owner;
    private final Comment comment;
    private final boolean isReply;

    private static final AtomicLong counter = new AtomicLong(300);

    public Notification(NotificationBuilder nb) {
        this.id = nb.id;
        this.owner = nb.owner;
        this.comment = nb.comment;
        this.isReply = nb.reply;
    }

    public Notification(User owner, Comment comment, boolean isReply) {
        Notification nf = new NotificationBuilder().id()
                .owner(owner)
                .comment(comment)
                .build();
        this.id = nf.getId();
        this.owner = nf.getOwner();
        this.comment = nf.getComment();
        this.isReply = nf.isReply();
    }

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean isReply() {
        return isReply;
    }

    @Override
    public String toString() {
        return "Notification ID: " + id
                + "\nowner: " + owner.getUserName()
                + "\ncomment at: " + comment.getTimeStamp()
                + "\ncomment from: " + comment.getAuthor().getUserName()
                + "\ncomment body: " + comment.getBody()
                + "\nis reply: " + isReply
                + "\n";
    }

    public static class NotificationBuilder {
        private long id;
        private User owner;
        private Comment comment;
        private boolean reply;

        public NotificationBuilder id() {
            this.id = Notification.counter.getAndIncrement();
            return this;
        }

        public NotificationBuilder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public NotificationBuilder comment(Comment comment) {
            this.comment = comment;
            return this;
        }

        public NotificationBuilder isReply(boolean isReply) {
            this.reply = isReply;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }
}
