package ssi1.integrated.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.security.AuthenticationService;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.security.dtos.AccessToken;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;
import ssi1.integrated.services.UserService;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5174", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
@RequestMapping("")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/login/microsoft")
    public ResponseEntity<AuthenticationResponse> authenticateMicrosoft(@RequestHeader(name = "Authorization", required = false) String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.ok(authService.MicrosoftGraphService(jwtToken));
    }



    @PostMapping("/token")
    public AccessToken instantAccess(@RequestHeader(name = "Authorization") String refreshToken) {
        String jwtToken = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        return authService.instantAccess(jwtPayload);
    }


}
