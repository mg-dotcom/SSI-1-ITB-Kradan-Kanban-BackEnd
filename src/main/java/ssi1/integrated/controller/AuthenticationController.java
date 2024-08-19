package ssi1.integrated.controller;

        import jakarta.validation.Valid;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;
        import ssi1.integrated.security.AuthenticationService;
        import ssi1.integrated.security.dtos.AuthenticationRequest;

@RestController
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
