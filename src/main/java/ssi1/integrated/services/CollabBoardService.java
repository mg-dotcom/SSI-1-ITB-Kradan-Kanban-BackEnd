package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.CollabManagement;
import ssi1.integrated.project_board.collab_management.CollabManagementRepository;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

import java.util.List;

import static ssi1.integrated.project_board.board.Visibility.PUBLIC;

@Service
public class CollabBoardService {
    private CollabManagementRepository collabManagementRepository;
    private BoardService boardService;
    private BoardRepository boardRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private JwtService jwtService;

    public List<CollabManagement> getAllCollabBoard(String jwtToken,String boardId){
        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
        if (board.getId() != null && (authorizationResult.isOwner() || authorizationResult.isPublic()) ){
            return collabManagementRepository.findAllByBoardId(boardId);
        }
        throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
    }

    public CollabManagement getCollaborator(String jwtToken,String boardId,String userOid){
        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
        CollabManagement collabManagementExist = collabManagementRepository.findByUserOid(userOid);
        if (collabManagementExist.getUser_oid() == userOid && (board.getVisibility() == PUBLIC || authorizationResult.isOwner())){
            return collabManagementRepository.findByUserOid(userOid);
        }
        throw new ForbiddenException("this UserOid doesnt exist!!");
    }

    public BoardAuthorizationResult authorizeBoardReadAccess(String boardId, String jwtToken) {
        Board board = boardService.getBoardById(boardId);

        // If the board is public, return immediately allowing access
        if (board.getVisibility() == PUBLIC) {
            return new BoardAuthorizationResult(false, true);  // Public board, ownership doesn't matter
        }

        User user = userService.getUserByOid(board.getUserOid());

        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);

        // Private board means isPublic should be false
        return new BoardAuthorizationResult(isOwner, false);
    }

    public BoardAuthorizationResult authorizeBoardModifyAccess(String boardId, String jwtToken) {
        Board board = boardService.getBoardById(boardId);

        User user = userService.getUserByOid(board.getUserOid());
        Visibility visibilityByBoardId = boardRepository.findVisibilityByBoardId(boardId);
        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);
        boolean isPublic = (visibilityByBoardId == PUBLIC);

        return new BoardAuthorizationResult(isOwner, isPublic);
    }
}
