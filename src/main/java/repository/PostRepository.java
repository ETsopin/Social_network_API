package repository;

import db.DatabaseConfig;
import model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PostRepository {

    public HashSet<Post> getLatestPosts(int limit) throws IllegalArgumentException {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        String sql = "SELECT a.id, creator_id, b.login, a.content, a.date_created FROM posts_schema.post a\n"
                + "JOIN users_schema.user b ON a.creator_id = b.id\n"
                + "ORDER BY date_created DESC\n"
                + "LIMIT ?\n";

        HashSet<Post> latestPosts = new HashSet<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setInt(1, limit);

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String id = rs.getString("id");
                    String content = rs.getString("content");
                    LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();
                    String creatorId = rs.getString("creator_id");
                    String creatorLogin = rs.getString("login");

                    latestPosts.add(new Post(id, creatorId, content, dateCreated, creatorLogin));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return latestPosts;
    }

    public HashSet<Post> getPostsByCreatorId(String creatorId) {
        HashSet<Post> creatorPosts = new HashSet<>();

        String sql = "SELECT a.id, content, date_created, b.login\n" +
                "FROM posts_schema.post a\n" +
                "LEFT JOIN users_schema.user b ON a.creator_id = b.id\n" +
                "WHERE creator_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, creatorId);

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String id = rs.getString("id");
                    String content = rs.getString("content");
                    LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();
                    String login = rs.getString("login");

                    creatorPosts.add(new Post(id, creatorId, content, dateCreated, login));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return creatorPosts;
    }

    public HashSet<Post> getUserFeed(String userId) {
        HashSet<Post> creatorPosts = new HashSet<>();

        String sql = "SELECT a.id, a.creator_id, b.login, a.content, a.date_created FROM posts_schema.post a \n" +
                "LEFT JOIN users_schema.user b ON  creator_id = b.id" +
                "    WHERE creator_id IN (\n" +
                "    SELECT subscription_id\n" +
                "    FROM users_schema.user_to_subscription\n" +
                "    WHERE user_id = ?\n" +
                ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, userId);

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String id = rs.getString("id");
                    String creatorId = rs.getString("creator_id");
                    String content = rs.getString("content");
                    LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();
                    String login = rs.getString("login");

                    creatorPosts.add(new Post(id, creatorId, content, dateCreated, login));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return creatorPosts;
    }

    public HashSet<Post> getPostsByContent(String contentPart) {
        HashSet<Post> creatorPosts = new HashSet<>();

        String sql = "SELECT a.id, a.creator_id, content, date_created, b.login\n" +
                "FROM posts_schema.post a\n" +
                "LEFT JOIN users_schema.user b ON a.creator_id = b.id\n" +
                "WHERE content LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Установка параметров запроса
            pstmt.setString(1, '%' + contentPart + '%'); // Устанавливаем возраст для условия

            // Выполнение запроса и получение ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // Обработка результатов
                while (rs.next()) {
                    String id = rs.getString("id");
                    String creatorId = rs.getString("creator_id");
                    String content = rs.getString("content");
                    LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();
                    String login = rs.getString("login");

                    creatorPosts.add(new Post(id, creatorId, content, dateCreated, login));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }

        return creatorPosts;
    }

    public void createPost(String creatorId, String content) {
        String sql = "INSERT INTO posts_schema.post (id, creator_id, content, date_created)" +
                "VALUES (?, ?, ?, ?)";

        String id = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setObject(2, creatorId);
            stmt.setObject(3, content);
            stmt.setObject(4, timestamp);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
        }
    }

}
