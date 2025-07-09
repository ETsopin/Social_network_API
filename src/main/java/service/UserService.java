package service;

import model.User;
import repository.UserRepository;
import util.PasswordHasher;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository = new UserRepository();


    public Boolean checkCredentialsByLogin(String userLogin, String userPassword) {

        Optional<String> userId = getUserIdByLogin(userLogin);

        if (userId.isEmpty()) {
            return Boolean.FALSE;
        }

        String passwordHash = PasswordHasher.hashPassword(userPassword);
        Optional<String> storedPasswordHash = userRepository.getPassword(userId.get());

        return storedPasswordHash.map(passwordHash::equals).orElse(Boolean.FALSE);

    }


    public Optional<User> registerUser(String login, String password) {
        if (userRepository.containsLogin(login)) {
            System.out.println("User with this login already exists");
            return Optional.empty();
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        String id = UUID.randomUUID().toString();
        User user = new User(id, login, passwordHash);
        try {
            userRepository.addUser(user);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }


        return Optional.of(user);
    }

    public Boolean isLoginTaken(String login) {
        if (userRepository.containsLogin(login)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    // Подписать юзера на таргет
    public void subscribeById(String userId, String targetUserId) {
        try {
            userRepository.subscribe(userId, targetUserId);
            System.out.printf("User %s successfully subscribed to user %s\n", userId, targetUserId);
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
        }
    }

    public void subscribeByLogin(String userLogin, String targetUserLogin) {
        Optional<String> userId = getUserIdByLogin(userLogin);
        Optional<String> targetUserId = getUserIdByLogin(targetUserLogin);

        if (userId.isEmpty() || targetUserId.isEmpty()) {
            System.out.println("Wrong login");
        } else {
            subscribeById(userId.get(), targetUserId.get());
        }
    }

    public HashSet<String> getUserSubscriptions(String login, String password) throws IllegalArgumentException{
        if (!checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Incorrect login or password");
        }

        Optional<String> userId = getUserIdByLogin(login);

        if (userId.isEmpty()) {
            throw new IllegalArgumentException("Login does not exist");
        }

        return userRepository.getUserSubscriptionsLogins(userId.get());

    }

    public HashSet<String> getUserSubscribers(String login, String password) throws IllegalArgumentException{
        if (!checkCredentialsByLogin(login, password)) {
            throw new IllegalArgumentException("Incorrect login or password");
        }

        Optional<String> userId = getUserIdByLogin(login);

        if (userId.isEmpty()) {
            throw new IllegalArgumentException("Login does not exist");
        }

        return userRepository.getUserSubscribersLogins(userId.get());

    }

    public Boolean isSubscribed(String userId, String targetId) {
        HashSet<String> subscriptions = userRepository.getUserSubscriptions(userId);

        if (subscriptions.contains(targetId)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    public Optional<String> getUserIdByLogin(String login) {
        return userRepository.getUserIdByLogin(login);
    }

}
