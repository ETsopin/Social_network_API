package model;

import java.util.HashSet;

public class User {
    private String id;
    private String login;
    private String password;
    // Subscriptions to other users
    private HashSet<String> subscriptions;
    private HashSet<Post> posts;
    // Subscription requests from other users
    private HashSet<String> subscriptionRequests;

    @Override
    public String toString() {
        String s = id + " " + login + " " + password + '\n';
        s += "Subscriptions: " + subscriptions + '\n';
        s += "Posts: " + posts  + '\n';
        s += "Subscription requests: " + subscriptionRequests + '\n';

        return s;
    }


    public User(String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.subscriptions = new HashSet<>();
        this.posts = new HashSet<>();
        this.subscriptionRequests = new HashSet<>();
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

    public HashSet<String> getUserSubscriptions() {
        return this.subscriptions;
    }

    public void addSubscription(String id) {
        this.subscriptions.add(id);
    }

    public void addSubscriptionRequest(String id) {
        this.subscriptionRequests.add(id);
    }

    public void removeSubscriptionRequest(String id) {
        this.subscriptionRequests.remove(id);
    }
}
