package ssi1.integrated.controller;

        import jakarta.validation.Valid;
        import lombok.RequiredArgsConstructor;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import ssi1.integrated.security.AuthenticationService;
        import ssi1.integrated.security.dtos.AuthenticationRequest;
        import ssi1.integrated.security.dtos.AuthenticationResponse;

@RestController
@CrossOrigin(origins = {"http://localhost:5174","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://localhost:5173"})
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        System.out.println(request);
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
