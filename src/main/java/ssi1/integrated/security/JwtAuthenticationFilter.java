package ssi1.integrated.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import io.jsonwebtoken.Claims;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.filter.OncePerRequestFilter;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.respond.ErrorResponse;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;
import ssi1.integrated.utils.UriExtractor;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    protected void doFilterInternal(@NonNull HttpServletRequest request //
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

        try {
            userName = jwtService.extractUsername(jwt);
        } catch (SignatureException e) {
            handleJwtException(response, request, "Token is tampered", HttpStatus.UNAUTHORIZED);
            return;
        } catch (MalformedJwtException e) {
            handleJwtException(response, request, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
            return;
        } catch (ExpiredJwtException e) {
            handleJwtException(response, request, "Token is expired", HttpStatus.UNAUTHORIZED);
            return;
        } catch (JwtException e) {
            handleJwtException(response, request, "Invalid token", HttpStatus.UNAUTHORIZED);
            return;
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // user not auth yet

        if (userName != null && authentication == null) { // when the Authentication is null means that the user not authenticated yet!
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName); //check form the Database

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpServletRequest request, HttpStatus status) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(), // Use the status passed as an argument
                message,
                request.getRequestURI()
        );
        response.setStatus(status.value()); // Set the response status to the provided status
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private void handleJwtException(HttpServletResponse response, HttpServletRequest request, String message, HttpStatus status) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();
            String boardId = UriExtractor.extractBoardId(uri);

            // Check if the board exists
            if (boardService.boardExists(boardId)) {
                sendErrorResponse(response, "Access denied to board with BOARD ID: " + boardId, request, HttpStatus.FORBIDDEN);
            } else {
                sendErrorResponse(response, "Board not found with BOARD ID: " + boardId, request, HttpStatus.NOT_FOUND);
            }
        } else {
            // For non-GET requests, send the error response with the provided message and status
            sendErrorResponse(response, message, request, status);
        }
    }



}
