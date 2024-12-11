package ssi1.integrated.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.project_board.user_local.UserLocalRepository;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLocalRepository userLocalRepository;
    @Autowired
    private ModelMapper modelMapper;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByOid(String oid) {
        User user= userRepository.findByOid(oid);
        if(user==null){
            UserLocal userLocal=userLocalRepository.findByOid(oid);
            user=modelMapper.map(userLocal,User.class);
        }
        return user;
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email, String accessTokenMS) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenMS);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = "https://graph.microsoft.com/v1.0/users/" + email;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            String jsonResponse = response.getBody();
            User microsoftUser = getUserFromResponse(jsonResponse);

            if (response.getStatusCode().is2xxSuccessful()) {
                return microsoftUser;
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            } else {
                throw e;
            }
        }

        return userRepository.findByEmail(email);
    }



    public User getUserFromResponse(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            User user=new User();
            user.setOid(rootNode.path("id").asText());
            user.setName(rootNode.path("displayName").asText());
            user.setUsername(rootNode.path("givenName").asText()+"."+rootNode.path("surname").asText());
            user.setEmail(rootNode.path("mail").asText());
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(username);
        if(user==null){
            return userLocalRepository.findByUsername(username);
        }
        return user;
    }
}
