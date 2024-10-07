package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.project_board.user_board.UserBoard;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;
import ssi1.integrated.project_board.user_board.UserBoardRepository;

import java.util.List;

@Service
public class UserBoardService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBoardRepository userBoardRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserBoard addUserToUserBoard(String oid) {
        User user = userRepository.findByOid(oid);
        if (user == null) {
            throw new IllegalArgumentException("User with OID " + oid + " does not exist.");
        }
        UserBoard userBoard = modelMapper.map(user, UserBoard.class);
        return userBoardRepository.save(userBoard);
    }

    public List<UserBoard> getAllUsersInUserBoard() {
        return userBoardRepository.findAll();
    }

    public UserBoard getUserInUserBoardByOid(String oid) {
        return userBoardRepository.findById(oid).orElse(null);
    }
}
