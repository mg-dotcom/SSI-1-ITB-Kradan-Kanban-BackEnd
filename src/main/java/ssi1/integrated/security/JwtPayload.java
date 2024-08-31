package ssi1.integrated.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtPayload {
    private String iss;
    private long iat;
    private long exp;
    private String name;
    private String oid;
    private String email;
    private String role;
    private String sub;
}
