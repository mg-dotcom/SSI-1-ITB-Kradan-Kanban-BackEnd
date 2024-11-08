package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.AccessRightDTO;
import ssi1.integrated.dtos.AddCollabBoardDTO;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.dtos.CollaboratorDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ConflictException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.collab_management.Status;
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
    @Autowired
    private EmailService emailService;

    public List<CollaboratorDTO> getAllCollabsBoard(String accessToken, String boardId){
        Board board = boardRepository.findById(boardId).orElseThrow(
        () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        List<CollabBoard> foundedCollabBoardLists = collabBoardRepository.findAllByBoardId(board.getId());
        List<CollaboratorDTO> collaboratorDTOList = new ArrayList<>();

        for (CollabBoard collabBoard: foundedCollabBoardLists){
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
            UserLocal foundedUserLocal = userLocalService.getUserByOid(collabBoard.getUser().getOid());
            collaboratorDTO.setOid(foundedUserLocal.getOid());
            collaboratorDTO.setName(foundedUserLocal.getName());
            collaboratorDTO.setEmail(foundedUserLocal.getEmail());
            collaboratorDTO.setAccessRight(collabBoard.getAccessRight());
            collaboratorDTO.setAddedOn(collabBoard.getAddedOn());

            collaboratorDTOList.add(collaboratorDTO);
        }

        Visibility visibility = board.getVisibility();
        if (visibility == Visibility.PUBLIC) {
            System.out.println("ok");
            return collaboratorDTOList;
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            System.out.println("okok");
            throw new AuthenticationException("JWT token is required.") {
            };
        }
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner &&!isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        return collaboratorDTOList;
    }

    public CollaboratorDTO getCollaborators(String accessToken, String boardId,String collabsOid){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));


        Visibility visibility = board.getVisibility();

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);
        if (visibility == PRIVATE && !isOwner && !isCollaborator) {
            throw new ForbiddenException("You do not have permission to access this board.");
        }

        CollabBoard collabBoard = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId,collabsOid);
        if(collabBoard==null){
            throw new ItemNotFoundException("Not found collaborator");
        }
        UserLocal foundedUserLocal = userLocalService.getUserByOid(collabsOid);

        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        collaboratorDTO.setOid(foundedUserLocal.getOid());
        collaboratorDTO.setName(foundedUserLocal.getName());
        collaboratorDTO.setEmail(foundedUserLocal.getEmail());
        collaboratorDTO.setAccessRight(collabBoard.getAccessRight());
        collaboratorDTO.setAddedOn(collabBoard.getAddedOn());

        if (visibility == Visibility.PUBLIC) {
            return collaboratorDTO;
        }

        return collaboratorDTO;
    }

    public CollabBoardDTO addCollabBoard(String accessToken, String boardId, AddCollabBoardDTO addCollabBoardDTO){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        Visibility visibility = board.getVisibility();

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner&& !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can add tasks.");
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
        }

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required.") {
            };
        }

        // Extract the JWT payload from the request
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        // Find the user associated with the OID from the JWT payload
        User userOwner = userService.getUserByOid(jwtPayload.getOid());
        if (!userOwner.getOid().equals(board.getUserOid())){
            throw new ForbiddenException("You do not have permission to modify this board.");
        }

        if (addCollabBoardDTO.getEmail().equals(userOwner.getEmail())){
            throw new ConflictException("The email belongs to the board owner.");
        }

        if (addCollabBoardDTO.getEmail() != null && addCollabBoardDTO.getAccessRight() == null) {
            throw new BadRequestException("Access right must not be null");
        }

        List<CollabBoard> existingCollabBoard = collabBoardRepository.findAllByBoardId(boardId);
        for (CollabBoard collabBoard : existingCollabBoard) {
            if (collabBoard.getUser().getEmail().equals(addCollabBoardDTO.getEmail())) {
                throw new ConflictException("The email belongs to an existing collaborator.");
            }
        }
        User foundedUserByEmail = userService.getUserByEmail(addCollabBoardDTO.getEmail());
        if (foundedUserByEmail == null) {
            throw new ItemNotFoundException("User Email Not Found with email: " + addCollabBoardDTO.getEmail());
        }

        UserLocal boardOwnername = userLocalService.getUserByOid(jwtPayload.getOid());

        emailService.sendEmail(boardId,addCollabBoardDTO.getEmail(),boardOwnername.getName(),addCollabBoardDTO.getAccessRight().toString().toUpperCase(),board.getName());
        UserLocal savedUserToLocal = userLocalService.addUserToUserLocal(foundedUserByEmail);

        CollabBoardDTO collabBoardDTO = new CollabBoardDTO();
            CollabBoard newCollabBoard = new CollabBoard();
            newCollabBoard.setUser(savedUserToLocal);
            newCollabBoard.setAccessRight(addCollabBoardDTO.getAccessRight());
            newCollabBoard.setBoard(board);
            newCollabBoard.setStatus(Status.PENDING);

            collabBoardDTO.setOid(savedUserToLocal.getOid());
            collabBoardDTO.setBoardId(boardId);
            collabBoardDTO.setName(savedUserToLocal.getName());
            collabBoardDTO.setEmail(addCollabBoardDTO.getEmail());
            collabBoardDTO.setAccessRight(addCollabBoardDTO.getAccessRight());
            System.out.println("Unsave.");
            collabBoardRepository.save(newCollabBoard);
            System.out.println("Saved." + newCollabBoard.getUser().getEmail());

        return collabBoardDTO;
    }

    @Transactional
    public CollabBoard updateCollaboratorAccessRight(String accessToken, String boardId, String collabsOid, AccessRightDTO accessRight){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        Visibility visibility = board.getVisibility();

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner&& !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can add tasks.");
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
        }

        if(accessRight.getAccessRight() == null){
            throw new BadRequestException("Access right must not be null");
        }

        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);

        CollabBoard collaborator=collabBoardRepository.findByBoard_IdAndUser_Oid(boardId,collabsOid);
        //404
        if(collaborator==null){
            throw new ItemNotFoundException("The "+collabsOid+" is not a collaborator on the board.");
        }

        //403
        if(!board.getUserOid().equals(jwtPayload.getOid())){
            throw new ForbiddenException("Only board owner can edit access right.");
        }

        //400 
        try {
            collaborator.setAccessRight(AccessRight.valueOf(accessRight.getAccessRight().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid access right provided.");
        }

        return collaborator;
    }

    

    @Transactional
    public void deleteCollaborator(String accessToken, String boardId,String collabsOid){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        Visibility visibility = board.getVisibility();

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner&& !isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaborator) {
            throw new ForbiddenException("Only board owner and collaborators with write access can add tasks.");
        }

        if (!isOwner && !isCollaborator) {
            throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
        }

        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);

        CollabBoard collaborator=collabBoardRepository.findByBoard_IdAndUser_Oid(boardId,collabsOid);


        //403
        if(!(jwtPayload.getOid().equals(board.getUserOid())||jwtPayload.getOid().equals(collaborator.getUser().getOid()))){
            throw new ForbiddenException("Only board owner can delete collaborators and only collaborator can delete themself");
        }

        //404
        if(collaborator==null){
            throw new ItemNotFoundException("The "+collabsOid+" is not a collaborator on the board.");
        }

        collabBoardRepository.delete(collaborator);
    }

    private boolean isBoardOwner(String userOid, String jwtToken) {
        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(userOid);
        return user.getOid().equals(jwtPayload.getOid());
    }

    // Check if collaborator has write access
    public boolean isCollaboratorWriteAccess(String jwtToken, String boardId) {
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());

        return collaborator != null && collaborator.getAccessRight() == AccessRight.WRITE;
    }

    public boolean isCollaborator(String jwtToken, String boardId){
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
        return collaborator!=null;
    }
}
