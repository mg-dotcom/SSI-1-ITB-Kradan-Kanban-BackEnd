package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

import java.util.List;

import static ssi1.integrated.project_board.board.Visibility.PUBLIC;

@Service
public class CollabBoardService {
    private CollabBoardRepository collabBoardRepository;
    private BoardService boardService;
    private BoardRepository boardRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private JwtService jwtService;



    public List<CollabBoard> getAllCollabBoard(String jwtToken, String boardId){
        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
        if (board.getId() != null && (authorizationResult.isOwner() || authorizationResult.isPublic()) ){
            return collabBoardRepository.findAllByBoardId(boardId);
        }
        throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
    }

//    public CollabManagement getCollaborator(String jwtToken,String boardId,String userOid){
////        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
////        Board board = boardRepository.findById(boardId).orElseThrow(
////                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
////        );
////        CollabManagement collabManagementExist = collabManagementRepository.findByUserOid(userOid);
////        if (collabManagementExist.getUser_oid() == userOid && (board.getVisibility() == PUBLIC || authorizationResult.isOwner())){
////            return collabManagementRepository.findByUserOid(userOid);
////        }
////        throw new ForbiddenException("this UserOid doesnt exist!!");
//
//        Board selectedBoard = boardRepository.findById(boardId).orElseThrow(
//                () -> new ItemNotFoundException("Board not found with board Id : " + boardId)
//        );
//
//    }


    public CollabBoardDTO addCollabBoard(String jwtToken, String boardId, String email, AccessRight access_right){
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
        User user = userService.getUserByEmail(email);
        if (user == null){
            throw new ItemNotFoundException("User Email Not Found with email" + email);
        }
//        boolean isCollab = collabBoardRepository.existsByUser_oidAndBoard_Id(user.getOid(), boardId);
        List<CollabBoard> existingCollabBoard = collabBoardRepository.findAllByBoardId(boardId);
        boolean collaboratorEmailExisting = false;
        for (CollabBoard collabBoard : existingCollabBoard) {
            if (collabBoard.getUser_oid().getEmail().equals(email)) {
                collaboratorEmailExisting = true;
                break;
            }
        }
//        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        CollabBoardDTO collabBoardDTO = new CollabBoardDTO();
        if (user.getEmail() != null && collaboratorEmailExisting == false && (access_right == AccessRight.WRITE || access_right == AccessRight.READ)) {
            CollabBoard newCollabBoard = new CollabBoard();
            newCollabBoard.setUser_oid(user);
            newCollabBoard.setAccessRight(access_right);
            newCollabBoard.setBoard(board);

            collabBoardDTO.setBoardID(boardId);
            collabBoardDTO.setCollaboratorName(user.getUsername());
            collabBoardDTO.setCollaboratorEmail(email);
            collabBoardDTO.setAccessRight(access_right);
        }
        return collabBoardDTO;
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
