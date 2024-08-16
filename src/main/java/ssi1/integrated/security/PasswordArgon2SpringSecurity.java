package ssi1.integrated.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordArgon2SpringSecurity {
    public static void main(String[] args) {
        Argon2PasswordEncoder encoder=new Argon2PasswordEncoder(9,16,1,16,2);
        String password = "Hello World";

        String hash = encoder.encode(password);
        System.out.println(hash);

        // argon2 verify hash
        if (encoder.matches("Hello World", hash)) {
            System.out.println("match");
        }

    }
}
