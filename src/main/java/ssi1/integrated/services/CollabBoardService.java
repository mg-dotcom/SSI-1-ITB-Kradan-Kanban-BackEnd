package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.AddCollabBoardDTO;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.dtos.CollaboratorDTO;
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

import java.util.ArrayList;
import java.util.List;

import static ssi1.integrated.project_board.board.Visibility.*;

@Service
public class CollabBoardService {
    @Autowired
    private CollabBoardRepository collabBoardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserLocalService userLocalService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtService jwtService;

    public List<CollaboratorDTO> getAllCollabsBoard(String jwtToken, String boardId){
                Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

               List<CollabBoard> foundedCollabBoardLists = collabBoardRepository.findAllByBoardId(boardId);
        List<CollaboratorDTO> collaboratorDTOList = new ArrayList<>();

                for (CollabBoard collabBoard: foundedCollabBoardLists){
                    CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
                    UserLocal foundedUserLocal = userLocalService.getUserByOid(collabBoard.getUser_oid().getOid());
                    collaboratorDTO.setUser_oid(foundedUserLocal.getOid());
                    collaboratorDTO.setCollaboratorName(foundedUserLocal.getName());
                    collaboratorDTO.setCollaboratorEmail(foundedUserLocal.getEmail());
                    collaboratorDTO.setAccessRight(collabBoard.getAccessRight());
                    collaboratorDTO.setAddedOn(collabBoard.getAddedOn());

                    collaboratorDTOList.add(collaboratorDTO);
                }
        return collaboratorDTOList;
    }

    public CollaboratorDTO getCollaborators(String jwtToken, String boardId,String collabsOid){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        if ( !(board.getVisibility().equals(PUBLIC) || authorizationResult.isOwner()) ) {
            throw new ForbiddenException("You do not have permission to access this board.");
        }

        // Fetch the collaborator details based on the provided collabOid
        CollabBoard collabBoard = collabBoardRepository.findByUser_oidAndBoard_Id(collabsOid, boardId);
        if (collabBoard == null) {
            throw new ItemNotFoundException("Collaborator not found for given board.");
        }

        UserLocal foundedUserLocal = userLocalService.getUserByOid(collabBoard.getUser_oid().getOid());
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        collaboratorDTO.setUser_oid(foundedUserLocal.getOid());
        collaboratorDTO.setCollaboratorName(foundedUserLocal.getName());
        collaboratorDTO.setCollaboratorEmail(foundedUserLocal.getEmail());
        collaboratorDTO.setAccessRight(collabBoard.getAccessRight());
        collaboratorDTO.setAddedOn(collabBoard.getAddedOn());

        return collaboratorDTO;
    }

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

            collabBoardDTO.setBoardId(boardId);
            collabBoardDTO.setCollaboratorName(savedUserToLocal.getUsername());
            collabBoardDTO.setCollaboratorEmail(addCollabBoardDTO.getEmail());
            collabBoardDTO.setAccessRight(addCollabBoardDTO.getAccessRight());
            collabBoardRepository.save(newCollabBoard);
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
