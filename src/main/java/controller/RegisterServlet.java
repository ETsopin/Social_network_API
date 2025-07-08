package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import service.CentralService;
import service.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final CentralService centralService = new CentralService();
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Читаем JSON из тела запроса
            JsonNode rootNode = mapper.readTree(request.getReader());

            // Извлекаем login и password
            String login = rootNode.path("login").asText();
            String password = rootNode.path("password").asText();

            // Валидация данных
            if (login == null || login.trim().isEmpty()) {
                responseMap.put("success", false);
                responseMap.put("message", "Login is required");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(), responseMap);
                return;
            }

            if (password == null || password.length() < 4) {
                responseMap.put("success", false);
                responseMap.put("message", "Password must be at least 6 characters");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(), responseMap);
                return;
            }

            // Проверка на существующего пользователя
            if (userService.isLoginTaken(login)) {
                responseMap.put("success", false);
                responseMap.put("message", "Login already taken");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                mapper.writeValue(response.getWriter(), responseMap);
                return;
            }

            // Регистрация пользователя
            if (centralService.registerUser(login, password)) {
                responseMap.put("success", true);
                responseMap.put("message", "User registered successfully");
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "Registration failed");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("success", false);
            responseMap.put("message", "Server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        mapper.writeValue(response.getWriter(), responseMap);
    }
}