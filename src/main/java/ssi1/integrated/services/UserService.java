package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUser(String userName){
        return userRepository.findByUsername(userName);
    }
}
