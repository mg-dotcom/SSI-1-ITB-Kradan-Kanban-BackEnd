package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;
import ssi1.integrated.project_board.user_local.UserLocalRepository;

import java.util.List;

@Service
public class UserLocalService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLocalRepository userLocalRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserLocal addUserToUserLocal(User user) {
        if (user == null) {
            throw new BadRequestException("User doesn't exist" + user);
        }
        UserLocal userLocal = modelMapper.map(user, UserLocal.class);
        return userLocalRepository.save(userLocal);
    }

    public List<UserLocal> getAllLocalUser() {
        return userLocalRepository.findAll();
    }

    public UserLocal getUserByOid(String oid) {
        return userLocalRepository.findById(oid).orElse(null);
    }
}
