package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.AddCollabBoardDTO;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.user_local.UserLocal;
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
    private UserLocalService userLocalService;
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


    public CollabBoardDTO addCollabBoard(String jwtToken, String boardId, AddCollabBoardDTO addCollabBoardDTO){
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        // find user in email and store in local user
        User foundedUserByEmail = userService.getUserByEmail(addCollabBoardDTO.getEmail());
        if (foundedUserByEmail == null){
            throw new ItemNotFoundException("User Email Not Found with email" + addCollabBoardDTO.getEmail());
        }
        UserLocal savedUserToLocal = userLocalService.addUserToUserLocal(foundedUserByEmail);

//      boolean isCollab = collabBoardRepository.existsByUser_oidAndBoard_Id(user.getOid(), boardId);
        List<CollabBoard> existingCollabBoard = collabBoardRepository.findAllByBoardId(boardId);
        boolean collaboratorEmailExisting = false;
        for (CollabBoard collabBoard : existingCollabBoard) {
            if (collabBoard.getUser_oid().getEmail().equals(addCollabBoardDTO.getEmail())) {
                collaboratorEmailExisting = true;
                break;
            }
        }
//        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        CollabBoardDTO collabBoardDTO = new CollabBoardDTO();
        if (savedUserToLocal.getEmail() != null && !collaboratorEmailExisting && (addCollabBoardDTO.getAccessRight() == AccessRight.WRITE || addCollabBoardDTO.getAccessRight() == AccessRight.READ)) {
            CollabBoard newCollabBoard = new CollabBoard();
            newCollabBoard.setUser_oid(savedUserToLocal);
            newCollabBoard.setAccessRight(addCollabBoardDTO.getAccessRight());
            newCollabBoard.setBoard(board);

            collabBoardDTO.setBoardID(boardId);
            collabBoardDTO.setCollaboratorName(savedUserToLocal.getUsername());
            collabBoardDTO.setCollaboratorEmail(addCollabBoardDTO.getEmail());
            collabBoardDTO.setAccessRight(addCollabBoardDTO.getAccessRight());
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
