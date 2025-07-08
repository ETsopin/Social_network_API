package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Post;
import service.PostService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@WebServlet("/posts/feed")
public class FeedServlet extends HttpServlet {
    private final PostService postService = new PostService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Читаем JSON из тела запроса
            JsonNode rootNode = mapper.readTree(request.getReader());

            String login = request.getParameter("login");
            String password = request.getParameter("password");

            if (login == null || password == null) {
                responseMap.put("success", false);
                responseMap.put("message", "Not all fields are present");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(), responseMap);
                return;
            }

            try {
                HashSet<Post> posts = postService.getUserFeed(login, password);
                responseMap.put("success", true);
                responseMap.put("posts", mapper.writeValueAsString(posts));
            } catch (IllegalArgumentException e) {
                responseMap.put("success", false);
                responseMap.put("message", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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