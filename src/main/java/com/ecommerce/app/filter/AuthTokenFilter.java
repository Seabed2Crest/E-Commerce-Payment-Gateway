package com.ecommerce.app.filter;

import com.ecommerce.app.schema.TenantContextHolder;
import com.ecommerce.app.service.servicelayer.token.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ecommerce.app.util.SecurityConstantsUrl.getWhiteListUrls;


@Component
@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    private static final String SLASH = "/";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            log.info("Request received for path: {}", path);

            if (isWhiteListed(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("Extracted JWT Token: {}", token);

                if (!authTokenService.isTokenExpired(token)) {
                    String tenantId = authTokenService.extractCustomerId(token);
                    log.info("Valid token for tenantId: {}", tenantId);

                    request.setAttribute("tenantId", tenantId);
                    TenantContextHolder.setTenantId(tenantId);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            tenantId, null, null
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                } else {
                    log.warn("Auth token is expired.");
                    sendUnauthorizedResponse(response, "Invalid or Expired Auth Token");
                }
                return;
            }

            log.warn("Missing or invalid Authorization header.");
            sendUnauthorizedResponse(response, "Missing or Invalid Authorization Header");

        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Invalid or expired token.");
        } finally {
            log.info("Clearing the tenant context...");
            TenantContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }


    private boolean isWhiteListed(String path) {
        for (String whiteListedPath : getWhiteListUrls()) {
            if (path.equals(whiteListedPath)) {
                if (!SLASH.equals(whiteListedPath)) {
                    log.info("Path '{}' is white-listed", path);
                }
                return true;
            }
        }
        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String jsonResponse = "{ \"error\": \"Unauthorized\", \"message\": \"" + message + "\" }";
        response.getWriter().write(jsonResponse);
    }
}
