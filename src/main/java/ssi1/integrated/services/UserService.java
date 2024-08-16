package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserDTO;
import ssi1.integrated.user_account.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

//    public User getUser(String userName){
//        return userRepository.findByUsername(userName);
//    }

    public UserDTO getUser(String userName){
        User user=userRepository.findByUsername(userName).orElseThrow();
        return modelMapper.map(user, UserDTO.class);
    }


    public boolean verifyPassword(String plainPassword,String hashedPassword){
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(9, 16, 1, 16, 2);
//        String newHash =passwordEncoder.encode(plainPassword);
        return passwordEncoder.matches(plainPassword,hashedPassword);
    }
}
