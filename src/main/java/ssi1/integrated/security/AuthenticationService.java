package ssi1.integrated.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ssi1.integrated.project_board.user_board.UserBoard;
import ssi1.integrated.security.dtos.AccessToken;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;
import ssi1.integrated.services.UserBoardService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserBoardService userBoardService; // Inject UserBoardService


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUserName(),
                request.getPassword()
        ));
        User user = userRepository.findByUsername(request.getUserName());
        if (user != null) {
            UserBoard userBoard = userBoardService.addUserToUserBoard(user.getOid());
            // Optionally, you can handle the userBoard object as needed here
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);


        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AccessToken instantAccess(JwtPayload jwtPayload) {
        User user = userRepository.findByOid(jwtPayload.getOid());
        return AccessToken.builder().accessToken(jwtService.generateToken(user)).build();
    }
}
