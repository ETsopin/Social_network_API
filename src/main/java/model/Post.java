package model;

import java.time.LocalDateTime;

public class Post {
    private String id;
    private String creatorId;
    private String creatorLogin;
    private String content;
    private LocalDateTime timestamp;

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

    public void setLogin(String login) {
        this.creatorLogin = login;
    }

    @Override
    public String toString() {
        String s = "Post: \n";
        s += "id: " + this.id + '\n';
        s += "creator_id: " + this.creatorId + '\n';
        s += "creator_login: " + this.creatorLogin + '\n';
        s += "content: " + this.content + '\n';
        s += "timestamp: " + this.timestamp.toString() + '\n';

        return s;
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
