package ssi1.integrated.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ssi1.integrated.exception.respond.ErrorResponse;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.user_account.UserRepository;
import ssi1.integrated.utils.UriExtractor;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String userName = null;
        String jwt = null;
        String uri = request.getRequestURI();
        String boardId = UriExtractor.extractBoardId(uri);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if ("GET".equalsIgnoreCase(request.getMethod()) && boardService.boardExists(boardId)) {
                Board board = boardService.getBoardById(boardId);
                if (board.getVisibility() == Visibility.PUBLIC) {
                    filterChain.doFilter(request, response); // Public access granted
                    return;
                } else {
                    sendErrorResponse(response, "Access denied to private board with ID: " + boardId, request, HttpStatus.FORBIDDEN);
                    return;
                }
            }
            if (request.getMethod().matches("POST|PUT|DELETE|PATCH")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        } else {
            jwt = authHeader.substring(7);
            try {
                userName = jwtService.extractUsername(jwt);
            } catch (JwtException e) {
                if (request.getMethod().matches("POST|PUT|DELETE|PATCH")) {
                    sendErrorResponse(response, e.getMessage(), request, HttpStatus.UNAUTHORIZED);
                    return;
                }
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userName != null && authentication == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
//@Override
//protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                @NonNull HttpServletResponse response,
//                                @NonNull FilterChain filterChain) throws ServletException, IOException {
//
//    // Allow access to /login without filtering
//    if (request.getRequestURI().equals("/login")) {
//        filterChain.doFilter(request, response);
//        return;
//    }
//
//    final String authHeader = request.getHeader("Authorization");
//    String userName = null;
//    String jwt = null;
//    String uri = request.getRequestURI();
//
//    // Extract boardId if the request is accessing a board
//    String boardId = UriExtractor.extractBoardId(uri);
//
//    // Check if there is no Authorization header or it does not start with "Bearer "
//    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//
//        // Handle access to public boards
//        if ("GET".equalsIgnoreCase(request.getMethod()) && boardService.boardExists(boardId)) {
//            Board board = boardService.getBoardById(boardId);
//            if (board.getVisibility() == Visibility.PUBLIC) {
//                // Public board access allowed, proceed without authentication
//                filterChain.doFilter(request, response);
//                return;
//            } else {
//                // Access to private boards requires authentication
//                sendErrorResponse(response, "Access denied to private board with ID: " + boardId, request, HttpStatus.FORBIDDEN);
//                return;
//            }
//        }
//
//        // For protected routes that modify resources (POST, PUT, DELETE, PATCH)
//        if (request.getMethod().matches("POST|PUT|DELETE|PATCH")) {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            return;
//        }
//
//        // Continue the chain for non-protected GET requests
//        filterChain.doFilter(request, response);
//        return;
//    } else {
//        // Authorization header exists, extract JWT token
//        jwt = authHeader.substring(7);
//
//        // Additional null or empty JWT check to avoid the IllegalArgumentException
//        if (jwt == null || jwt.trim().isEmpty()) {
//            // Return 401 if JWT is missing or malformed
//            sendErrorResponse(response, "JWT is missing or malformed", request, HttpStatus.UNAUTHORIZED);
//            return;
//        }
//
//        try {
//            // Extract username from the JWT token
//            userName = jwtService.extractUsername(jwt);
//        } catch (JwtException e) {
//            // Handle JWT extraction failure
//            if (request.getMethod().matches("POST|PUT|DELETE|PATCH")) {
//                sendErrorResponse(response, e.getMessage(), request, HttpStatus.UNAUTHORIZED);
//                return;
//            }
//        }
//    }
//
//    // Check if the user is not authenticated but JWT token is present
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    if (userName != null && authentication == null) {
//        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
//        if (jwtService.isTokenValid(jwt, userDetails)) {
//            // If token is valid, authenticate the user
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                    userDetails,
//                    null,
//                    userDetails.getAuthorities()
//            );
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        } else {
//            // If token is invalid, return unauthorized status
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            return;
//        }
//    }
//
//    // Continue with the filter chain if the user is authenticated or public access is allowed
//    filterChain.doFilter(request, response);
//}
//

    private void sendErrorResponse(HttpServletResponse response, String message, HttpServletRequest request, HttpStatus status) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}

