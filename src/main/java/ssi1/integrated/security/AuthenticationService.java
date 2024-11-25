package ssi1.integrated.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ssi1.integrated.project_board.microsoftUser.MicrosoftUser;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.project_board.user_local.UserLocalRepository;
import ssi1.integrated.security.dtos.AccessToken;
import ssi1.integrated.security.dtos.AuthenticationRequest;
import ssi1.integrated.security.dtos.AuthenticationResponse;
import ssi1.integrated.services.UserLocalService;
import ssi1.integrated.services.UserService;
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
    private final ModelMapper modelMapper;
    private final UserService userService;

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
                ObjectMapper objectMapper = new ObjectMapper();
                MicrosoftUser microsoftUser = objectMapper.readValue(response.getBody(), MicrosoftUser.class);

                if (microsoftUser.getId() == null || microsoftUser.getDisplayName() == null) {
                    throw new RuntimeException("Incomplete Microsoft user data received.");
                }

                User existingUser=userService.getUserByOid(microsoftUser.getId());
                UserLocal userLocal=userLocalRepository.findByOid(existingUser.getOid());
                if(userLocal==null){
                    userLocalService.addUserToUserLocal(existingUser);
                }

                // Generate tokens
                var jwtToken = jwtService.generateToken(existingUser);
                var refreshToken = jwtService.generateRefreshToken(existingUser);

                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                throw new RuntimeException("Failed to fetch user profile. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Microsoft Graph API: " + e.getMessage(), e);
        }
    }

    public MicrosoftUser getUserFromMicrosoftGraph(String email, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            String url = "https://graph.microsoft.com/v1.0/users?$filter=mail eq '" + email + "'";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode userNode = root.get("value").elements().next();

                if (userNode != null) {
                    return objectMapper.treeToValue(userNode, MicrosoftUser.class);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user from Microsoft Graph API: " + e.getMessage(), e);
        }

        return null;
    }



    public AccessToken instantAccess(JwtPayload jwtPayload) {
        User user = userRepository.findByOid(jwtPayload.getOid());
        return AccessToken.builder().accessToken(jwtService.generateToken(user)).build();
    }
}
