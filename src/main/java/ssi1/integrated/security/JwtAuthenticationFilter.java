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

    private void sendErrorResponse(HttpServletResponse response, String message, HttpServletRequest request, HttpStatus status) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}

