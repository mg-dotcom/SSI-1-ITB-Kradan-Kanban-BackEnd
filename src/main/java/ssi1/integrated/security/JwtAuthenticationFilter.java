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

import java.io.IOException;
import java.util.List;


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
            handleJwtException(response, request, "Token is tampered", HttpStatus.BAD_REQUEST);
            return;
        } catch (MalformedJwtException e) {
            handleJwtException(response, request, "Malformed JWT token", HttpStatus.BAD_REQUEST);
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

    private void sendErrorResponse(HttpServletResponse response, String message, HttpServletRequest request) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                request.getRequestURI()
        );
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }


    private void handleJwtException(HttpServletResponse response, HttpServletRequest request, String message, HttpStatus status) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            // If it's a GET request, return a 400 Bad Request for specific exceptions
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            sendErrorResponse(response,message,request);
        }
    }
}
