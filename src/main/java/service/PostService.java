package service;

import db.DatabaseConfig;
import model.Post;
import repository.PostRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostService() {
        this.postRepository = new PostRepository();
        this.userService = new UserService();
    }

    public HashSet<Post> getUserPosts(String userId) {
        return postRepository.getPostsByCreatorId(userId);
    }

    public HashSet<Post> getLatestPosts(int limit) throws IllegalArgumentException{
        return postRepository.getLatestPosts(limit);
    }

    public HashSet<Post> getPostsByCreatorLogin(String userLogin) throws IllegalArgumentException{
        Optional<String> userId = userService.getUserIdByLogin(userLogin);

        if (userId.isPresent()){
            return postRepository.getPostsByCreatorId(userId.get());
        } else {
            throw new IllegalArgumentException("User with this login does not exist");
        }
    }

    public void createPost(String login, String password, String content) throws IllegalArgumentException {
        if (!userService.checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Invalid login or password");
        }

        Optional<String> userId = userService.getUserIdByLogin(login);

        if (userId.isEmpty()) {
            throw new IllegalArgumentException("Login does not exist");
        }

        postRepository.createPost(userId.get(), content);

    }

    public HashSet<Post> getUserFeed(String login, String password) throws IllegalArgumentException {
        if (!userService.checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Invalid login or password");
        }

        Optional<String> userId = userService.getUserIdByLogin(login);

        if (userId.isEmpty()) {
            throw new IllegalArgumentException("Login does not exist");
        }

        return postRepository.getUserFeed(userId.get());

    }

    public HashSet<Post> getPostsByContent(String contentPart) {
        return postRepository.getPostsByContent(contentPart);
    }


}
