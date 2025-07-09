package model;

import java.time.LocalDateTime;

public class Post {
    private final String id;
    private final String creatorId;
    private String creatorLogin;
    private final String content;
    private final LocalDateTime timestamp;

    public Post(String id, String creatorId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.creatorId = creatorId;
        this.content = content;
        this.timestamp = timestamp;
        this.creatorLogin = "";
    }

    public Post(String id, String creatorId, String content, LocalDateTime timestamp, String login) {
        this.id = id;
        this.creatorId = creatorId;
        this.content = content;
        this.timestamp = timestamp;
        this.creatorLogin = login;
    }

    public Post() {
        this.id = "";
        this.creatorId = "";
        this.content = "";
        this.timestamp = null;
    }

    public String getId() {
        return this.id;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getContent() {
        return this.content;
    }

    public String getCreatorLogin() {
        return this.creatorLogin;
    }

}
