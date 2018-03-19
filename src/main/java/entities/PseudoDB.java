package entities;

import java.util.concurrent.CopyOnWriteArrayList;

public class PseudoDB {

    private static final CopyOnWriteArrayList<User> userList = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Comment> commentList = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Photo> photoList = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Notification> notificationList = new CopyOnWriteArrayList<>();


    static {
        User nicky = new User.UserBuilder().id()
                .userName("nicky94")
                .passwordSha256Hex("nickyisAwEsOmE23")
                .isAdmin(false)
                .comments(null)
                .notifications(null)
                .photos(null)
                .build();

        User bear = new User.UserBuilder().id()
                .userName("PolarBearAdmirer")
                .passwordSha256Hex("GreeNPEAce!!!")
                .isAdmin(false)
                .comments(null)
                .notifications(null)
                .photos(null)
                .build();

        User aaron = new User.UserBuilder().id()
                .userName("Aaron")
                .passwordSha256Hex("bij12Ad.q4£")
                .isAdmin(true)
                .comments(null)
                .notifications(null)
                .photos(null)
                .build();

        User sam = new User.UserBuilder().id()
                .userName("Sam")
                .passwordSha256Hex("NSA420swag")
                .isAdmin(false)
                .comments(null)
                .notifications(null)
                .photos(null)
                .build();

        Photo nickyPhoto = new Photo.PhotoBuilder().id()
                .author(nicky)
                .name("kitten")
                .comments(null)
                .build();

        Photo bearPhoto = new Photo.PhotoBuilder().id()
                .author(bear)
                .name("polar bears rule")
                .comments(null)
                .build();

        Photo aaronPhoto = new Photo.PhotoBuilder().id()
                .author(aaron)
                .name("summer vacation Ibiza")
                .comments(null)
                .build();

        Photo samPhoto = new Photo.PhotoBuilder().id()
                .author(sam)
                .name("chilling")
                .comments(null)
                .build();

        Comment samToNicky = new Comment.CommentBuilder().id()
                .author(sam)
                .timeStamp()
                .body("didn't know you have a cat O.o")
                .reply(null)
                .upVote()
                .read(true)
                .build();

        Comment nickyRepliesSam = new Comment.CommentBuilder().id()
                .author(nicky)
                .timeStamp()
                .body("yeah, got it couple weeks ago as a present!")
                .reply(null)
                .upVote()
                .read(true)
                .build();

        Comment samRepliesNicky = new Comment.CommentBuilder().id()
                .author(sam)
                .timeStamp()
                .body("nice, it's really cute!")
                .reply(null)
                .upVote()
                .read(false)
                .build();

        Comment aaronCommentsNicky = new Comment.CommentBuilder().id()
                .author(aaron)
                .timeStamp()
                .body("wait, was it your birthday not long ago?")
                .reply(null)
                .downVote()
                .read(true)
                .build();

        Comment nickyRepliesAaron = new Comment.CommentBuilder().id()
                .author(nicky)
                .timeStamp()
                .body("yes! and someone forgot about it -_-")
                .reply(null)
                .read(false)
                .build();


        Comment nickyToAaron = new Comment.CommentBuilder().id()
                .timeStamp()
                .author(nicky)
                .body("awesome views!")
                .reply(null)
                .read(false)
                .build();

        Notification samReplyNotification = new Notification.NotificationBuilder().id()
                .owner(sam)
                .isReply(true)
                .comment(nickyRepliesSam)
                .build();

        Notification nickyReplyNotif = new Notification.NotificationBuilder().id()
                .owner(nicky)
                .isReply(true)
                .comment(samRepliesNicky)
                .build();

        Notification aaronNewCommNotif = new Notification.NotificationBuilder().id()
                .owner(aaron)
                .comment(nickyToAaron)
                .isReply(false)
                .build();

        Notification aaronReplyNotif = new Notification.NotificationBuilder().id()
                .owner(aaron)
                .comment(nickyRepliesAaron)
                .isReply(true)
                .build();

        samToNicky.getReplies().add(nickyRepliesSam);
        samToNicky.getReplies().add(samRepliesNicky);
        aaronCommentsNicky.getReplies().add(nickyRepliesAaron);
        nicky.getComments().add(nickyRepliesSam);
        nicky.getComments().add(nickyToAaron);
        nicky.getComments().add(nickyRepliesAaron);
        aaron.getComments().add(aaronCommentsNicky);
        sam.getComments().add(samToNicky);
        sam.getComments().add(samRepliesNicky);

        nickyPhoto.getComments().add(samToNicky);
        nickyPhoto.getComments().add(aaronCommentsNicky);
        aaronPhoto.getComments().add(nickyToAaron);

        sam.getNotifications().add(samReplyNotification);
        nicky.getNotifications().add(nickyReplyNotif);
        aaron.getNotifications().add(aaronNewCommNotif);
        aaron.getNotifications().add(aaronReplyNotif);

        nicky.getPhotos().add(nickyPhoto);
        bear.getPhotos().add(bearPhoto);
        aaron.getPhotos().add(aaronPhoto);
        sam.getPhotos().add(samPhoto);

        userList.add(nicky);
        userList.add(bear);
        userList.add(aaron);
        userList.add(sam);

        commentList.add(samToNicky);
        commentList.add(nickyRepliesSam);
        commentList.add(nickyToAaron);
        commentList.add(aaronCommentsNicky);
        commentList.add(nickyRepliesAaron);

        photoList.add(nickyPhoto);
        photoList.add(samPhoto);
        photoList.add(bearPhoto);
        photoList.add(aaronPhoto);

        notificationList.add(samReplyNotification);
        notificationList.add(nickyReplyNotif);
        notificationList.add(aaronNewCommNotif);
        notificationList.add(aaronReplyNotif);

    }

    private PseudoDB() {}

    public static CopyOnWriteArrayList<User> getUsers() {
        return userList;
    }

    public static CopyOnWriteArrayList<Photo> getPhotos() {
        return photoList;
    }

    public static CopyOnWriteArrayList<Comment> getComments() {
        return commentList;
    }

    public static CopyOnWriteArrayList<Notification> getNotifications() {
        return notificationList;
    }
}
