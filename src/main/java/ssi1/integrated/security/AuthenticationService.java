package ssi1.integrated.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;
import ssi1.integrated.user_account.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Object authenticationResponse(AuthenticationRequest request){
        System.out.println("Before auth");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        System.out.println("After auth");
        return new Object();
    }
}
