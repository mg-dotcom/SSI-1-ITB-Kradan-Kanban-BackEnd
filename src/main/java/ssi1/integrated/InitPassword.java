package ssi1.integrated;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;

public class InitPassword {
    public static void main(String[] args) {
        Argon2 argon2= Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d,16,16);
//        UserRepository userRepository ;
//        char[] password;
//        for(User u : userRepository.findAll()){
//
//        }
    }
}
