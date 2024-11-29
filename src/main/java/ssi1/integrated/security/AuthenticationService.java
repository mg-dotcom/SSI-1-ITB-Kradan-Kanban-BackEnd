package ssi1.integrated.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.project_board.user_local.UserLocalRepository;
import ssi1.integrated.security.dtos.AccessToken;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;
import ssi1.integrated.services.UserLocalService;
import ssi1.integrated.services.UserService;
import ssi1.integrated.user_account.Role;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserLocalRepository userLocalRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserLocalService userLocalService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUserName(),
                request.getPassword()
        ));
        User user = userRepository.findByUsername(request.getUserName());
        if (user != null) {
            userLocalService.addUserToUserLocal(user);
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);


        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserLocal getUserFromResponse(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            UserLocal userLocal=new UserLocal();
            userLocal.setOid(rootNode.path("id").asText());
            userLocal.setName(rootNode.path("displayName").asText());
            userLocal.setUsername(rootNode.path("givenName").asText()+"."+rootNode.path("surname").asText());
            userLocal.setEmail(rootNode.path("mail").asText());
            return userLocal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }

    public AuthenticationResponse MicrosoftGraphService(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Set Bearer token in the headers
        System.out.println("Microsoft service");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://graph.microsoft.com/v1.0/me",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Deserialize the Microsoft Graph API response
                String jsonResponse = response.getBody();
                UserLocal microsoftUser=getUserFromResponse(jsonResponse);

                if (microsoftUser.getOid() == null ) {
                    throw new ItemNotFoundException("Not found this microsoft user");
                }

                User existingUser=userRepository.findByOid(microsoftUser.getOid());
                if(existingUser==null){
                    User newUser=modelMapper.map(microsoftUser,User.class);
                    newUser.setRole(Role.STUDENT);
                    userLocalService.addUserToUserLocal(newUser);
                    System.out.println(newUser);
                    var jwtToken = jwtService.generateToken(newUser);
                    var refreshToken = jwtService.generateRefreshToken(newUser);

                    return AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .build();

                }else {
                    System.out.println("case 2");
                    userLocalService.addUserToUserLocal(existingUser);
                    // Generate tokens
                    var jwtToken = jwtService.generateToken(existingUser);
                    var refreshToken = jwtService.generateRefreshToken(existingUser);

                    return AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .build();
                }


            } else {
                throw new RuntimeException("Failed to fetch user profile. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new AuthenticationException("Invalid access token", e) {
            };
        }
    }

    public AccessToken instantAccess(JwtPayload jwtPayload) {
        User user = userRepository.findByOid(jwtPayload.getOid());
        return AccessToken.builder().accessToken(jwtService.generateToken(user)).build();
    }
}
