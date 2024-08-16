package ssi1.integrated.security.dtos;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
