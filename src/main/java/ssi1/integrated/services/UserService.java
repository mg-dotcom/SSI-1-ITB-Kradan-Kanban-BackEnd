package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByOid(String oid) {
        return userRepository.findByOid(oid);
    }
}
