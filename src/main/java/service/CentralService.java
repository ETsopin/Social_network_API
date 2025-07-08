package service;

import model.Post;
import model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.Flow;

public class CentralService {
    private final UserService userService;
    private final PostService postService;

    public CentralService(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public CentralService() {
        this.userService = new UserService();
        this.postService = new PostService();
    }

    // User actions
    // Registration and subscription
    public Boolean registerUser(String login, String password) {
        Optional<User> user = userService.registerUser(login, password);

        if (user.isEmpty()) {
            return Boolean.FALSE;
        } else {
            System.out.printf("User %s with password %s successfully registered", login, password);
            return Boolean.TRUE;
        }
    }

    public void subscribe(String login, String password, String targetLogin) throws IllegalArgumentException{
        if (!userService.checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Incorrect login or password");
        }

        userService.subscribeByLogin(login, targetLogin);
    }

    public void getMyPosts(String userId) {
        HashSet<Post> posts = postService.getUserPosts(userId);
        System.out.println(posts);
    }

    public HashSet<Post> getLatestPosts(int limit) throws IllegalArgumentException{
        return postService.getLatestPosts(limit);
    }

    public HashSet<Post> getMyPosts(String login, String password) throws IllegalArgumentException{
        Boolean isCredentialsValid = userService.checkCredentialsByLogin(login, password);

        if (!isCredentialsValid) {
            throw new IllegalArgumentException("Incorrect login or password");
        }

        return postService.getPostsByCreatorLogin(login);
    }

    public HashSet<Post> getSubscriberPosts(String login, String password, String subscriberLogin)
            throws IllegalArgumentException{
        if (!userService.checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Incorrect login or password");
        }

        Optional<String> userId = userService.getUserIdByLogin(login);
        Optional<String> subscriberId = userService.getUserIdByLogin(subscriberLogin);

        if (userId.isEmpty() || subscriberId.isEmpty()) {

            throw new IllegalArgumentException("Login does not exist");
        }

        if (!userService.isSubscribed(subscriberId.get(), userId.get())) {
            throw new IllegalArgumentException("User is not subscribed");
        }

        return postService.getPostsByCreatorLogin(subscriberLogin);

    }

    public Boolean isLoginTaken(String login) {
        return userService.isLoginTaken(login);
    }

}
