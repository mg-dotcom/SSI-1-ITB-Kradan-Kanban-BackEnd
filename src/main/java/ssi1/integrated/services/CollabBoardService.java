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
import ssi1.integrated.security.JwtPayload;
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

        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        if ( !(board.getVisibility().equals(PUBLIC) || authorizationResult.isOwner()) ) {
            throw new ForbiddenException("You do not have permission to access this board.");
        }

               List<CollabBoard> foundedCollabBoardLists = collabBoardRepository.findAllByBoardId(boardId);
        List<CollaboratorDTO> collaboratorDTOList = new ArrayList<>();

                for (CollabBoard collabBoard: foundedCollabBoardLists){
                    CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
                    UserLocal foundedUserLocal = userLocalService.getUserByOid(collabBoard.getUser().getOid());
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



        // Fetch the collaborator details based on the provided collabOid
        CollabBoard collabBoard = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId,collabsOid);

        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        if ( !(board.getVisibility().equals(PUBLIC) || authorizationResult.isOwner()) && !collabBoard.getUser().getOid().equals(collabsOid)) {
            throw new ForbiddenException("You do not have permission to access this board.");
        }
        if (collabBoard == null) {
            throw new ItemNotFoundException("Collaborator not found for given board.");
        }
        UserLocal foundedUserLocal = userLocalService.getUserByOid(collabsOid);

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
//        // Extract the JWT payload from the request
//        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
//        // Find the user associated with the OID from the JWT payload
//        User user = userService.getUserByOid(jwtPayload.getOid());
        // find user in email and store in local user
        User foundedUserByEmail = userService.getUserByEmail(addCollabBoardDTO.getEmail());
        System.out.println("Founded USER Id is : " + foundedUserByEmail.getOid());

        if (foundedUserByEmail == null){
            throw new ItemNotFoundException("User Email Not Found with email" + addCollabBoardDTO.getEmail());
        }
        if ( !(addCollabBoardDTO.getAccessRight().equals(AccessRight.WRITE) || addCollabBoardDTO.getAccessRight().equals(AccessRight.READ))){
            throw new ItemNotFoundException("The AccessRight wasn't WRITE or READ");
        }

        UserLocal savedUserToLocal = userLocalService.addUserToUserLocal(foundedUserByEmail);

        System.out.println("Saved User Email : "+savedUserToLocal.getEmail());
        List<CollabBoard> existingCollabBoard = collabBoardRepository.findAllByBoardId(boardId);
        boolean collaboratorEmailExisting = false;
        for (CollabBoard collabBoard : existingCollabBoard) {
            if (collabBoard.getUser().getEmail().equals(addCollabBoardDTO.getEmail())) {
                collaboratorEmailExisting = true;
                break;
            }
        }
//        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);
        CollabBoardDTO collabBoardDTO = new CollabBoardDTO();
        if (savedUserToLocal.getEmail() != null && !collaboratorEmailExisting) {
            CollabBoard newCollabBoard = new CollabBoard();
            newCollabBoard.setUser(savedUserToLocal);
            newCollabBoard.setAccessRight(addCollabBoardDTO.getAccessRight());
            newCollabBoard.setBoard(board);

            collabBoardDTO.setBoardId(boardId);
            collabBoardDTO.setCollaboratorName(savedUserToLocal.getUsername());
            collabBoardDTO.setCollaboratorEmail(addCollabBoardDTO.getEmail());
            collabBoardDTO.setAccessRight(addCollabBoardDTO.getAccessRight());
            System.out.println("Unsave.");
            collabBoardRepository.save(newCollabBoard);
            System.out.println("Saved." + newCollabBoard.getUser().getEmail());
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
