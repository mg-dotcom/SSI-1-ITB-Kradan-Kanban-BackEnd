package ssi1.integrated.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.security.AuthenticationService;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.security.dtos.AccessToken;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;

@RestController
@CrossOrigin(origins = {"http://localhost:5174", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
@RequestMapping("")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/login/microsoft")
    public String authenticateMicrosoft(@RequestHeader(name = "Authorization") String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return authService.MicrosoftGraphService(jwtToken);
    }

    @PostMapping("/token")
    public AccessToken instantAccess(@RequestHeader(name = "Authorization") String refreshToken) {
        String jwtToken = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        return authService.instantAccess(jwtPayload);
    }
}
