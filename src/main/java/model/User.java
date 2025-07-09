package model;

import java.util.HashSet;

public class User {
    private final String id;
    private final String login;
    private final String password;


    public User(String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public String getPassword () {
        return this.password;
    }

    public String getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }
}
