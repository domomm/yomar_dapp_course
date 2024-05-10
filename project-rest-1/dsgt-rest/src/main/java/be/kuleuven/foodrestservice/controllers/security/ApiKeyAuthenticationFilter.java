package be.kuleuven.foodrestservice.controllers.security;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    // Implementation of API key authentication filter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        String apiKey = extractApiKey(request);

        if (isValidApiKey(apiKey)) {
            Authentication authentication = new ApiKeyAuthentication(apiKey);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("API key is missing or incorrect");
            response.getWriter().flush();
        }
    }

    private String extractApiKey(HttpServletRequest request) {
        // Extract API key from request header or query parameter
        // Example: String apiKey = request.getHeader("Api-Key");
        return request.getHeader("Api-Key");
    }

    private boolean isValidApiKey(String apiKey) {
        // Implement your logic to validate the API key
        // Example: return apiKey != null && apiKey.equals("your-api-key");
        return apiKey != null && apiKey.equals("rasimrasim14");
    }
}

