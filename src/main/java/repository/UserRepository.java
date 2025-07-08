package repository;

import db.DatabaseConfig;
import model.Post;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    public Optional<String> getUserIdByLogin(String login) {
        Optional<String> resultId = Optional.empty();

        String sql = "SELECT id::TEXT FROM users_schema.user WHERE login = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, login); // Устанавливаем возраст для условия

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                if (rs.next()) {
                    String id = rs.getString("id");
                    resultId = Optional.of(id);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return resultId;
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users_schema.user (id, login, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, user.getId());
            stmt.setObject(2, user.getLogin());
            stmt.setObject(3, user.getPassword());

            stmt.execute();
        }

    }

    public Boolean containsLogin(String login) {
        String sql = "SELECT login FROM users_schema.user WHERE login = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, login);

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                if (rs.next())
                    return Boolean.TRUE;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    public Boolean checkCredentials(String userId, String password) {
        String sql = "SELECT password FROM users_schema.user WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, userId);

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                if (!rs.next()) {
                    return Boolean.FALSE;
                } else {
                    String pass = rs.getString("password");
                    if (!pass.equals(password)) {
                        return Boolean.FALSE;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }

    public HashSet<String> getUserSubscriptions(String userId) {
        HashSet<String> userSubscriptions = new HashSet<>();

        String sql = "SELECT subscription_id FROM users_schema.user_to_subscription WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, userId); // Устанавливаем возраст для условия

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String subscription_id = rs.getString("subscription_id");

                    userSubscriptions.add(subscription_id);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return userSubscriptions;
    }

    public HashSet<String> getUserSubscriptionsLogins(String userId) {
        HashSet<String> userSubscriptions = new HashSet<>();

        String sql = "SELECT login " +
                "FROM users_schema.user_to_subscription a " +
                "LEFT JOIN users_schema.user b ON a.subscription_id = b.id " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, userId); // Устанавливаем возраст для условия

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String login = rs.getString("login");

                    userSubscriptions.add(login);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return userSubscriptions;
    }

    public HashSet<String> getUserSubscribersLogins(String userId) {
        HashSet<String> userSubscriptions = new HashSet<>();

        String sql = "SELECT login " +
                "FROM users_schema.user_to_subscription a " +
                "LEFT JOIN users_schema.user b ON a.user_id = b.id " +
                "WHERE subscription_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, userId); // Устанавливаем возраст для условия

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String login = rs.getString("login");

                    userSubscriptions.add(login);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return userSubscriptions;
    }

    public void subscribe(String userId, String targetUserId) throws IllegalArgumentException {
        if (getUserSubscriptions(userId).contains(targetUserId)) {
            throw new IllegalArgumentException("User already subscribed");
        }

        String sql = "INSERT INTO users_schema.user_to_subscription (user_id, subscription_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, targetUserId);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }
    }

}
