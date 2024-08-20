package ssi1.integrated.controller;

        import jakarta.validation.Valid;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import ssi1.integrated.security.AuthenticationService;
        import ssi1.integrated.security.dtos.AuthenticationRequest;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService service;
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        service.authenticate(request);
        return new ResponseEntity<>("User login successful", HttpStatus.OK);
    }
}
