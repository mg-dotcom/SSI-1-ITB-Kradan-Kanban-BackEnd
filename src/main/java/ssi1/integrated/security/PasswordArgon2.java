package ssi1.integrated.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordArgon2 {
    public static void main(String[] args) {
        Argon2PasswordEncoder encoder=new Argon2PasswordEncoder(16,32,1,16384,2);

        String password = "HelloWorld";


        String hash = encoder.encode(password);
        System.out.println(hash);

        // argon2 verify hash
        /*if (encoder.matches("Hello World", hash)) {
            System.out.println("match");
        }*/



    }
}
