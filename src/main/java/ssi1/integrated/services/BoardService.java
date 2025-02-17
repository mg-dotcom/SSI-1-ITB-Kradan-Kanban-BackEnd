package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.*;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private JwtService jwtService;
    private StatusService statusService;
    private TaskRepository taskRepository;
    private StatusRepository statusRepository;
    private CollabBoardRepository collabBoardRepository;
    private UserLocalService userLocalService;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public AllBoardDTO getAllBoards(String token) {
        JwtPayload jwtPayload = jwtService.extractPayload(token);

        if (jwtPayload == null) {
            throw new IllegalStateException("JWT Payload is null");
        }

        User user = userService.getUserByOid(jwtPayload.getOid());
        UserLocal userLocal=userLocalService.getUserByOid(jwtPayload.getOid());
        String userOid= user.getOid()==null? userLocal.getOid() : user.getOid();
        List<Board> toReturnPersonalBoard = boardRepository.findAllByUserOidOrderByCreatedOnAsc(userOid);
        List<CollabBoard> listCollabsBoard = collabBoardRepository.findByUser_OidOrderByAddedOnAsc(userOid);

        ArrayList<ContributorBoardDTO> collabsBoardDTOs = new ArrayList<>();
        for (CollabBoard eachCollabsBoard: listCollabsBoard){
            UserLocal foundedUserLocal=userLocalService.getUserByOid(eachCollabsBoard.getUser().getOid());
            ContributorBoardDTO contributorBoardDTO = new ContributorBoardDTO();
            Optional<Board> board = boardRepository.findById(eachCollabsBoard.getBoard().getId());
            User ownerOfBoard = userService.getUserByOid(board.get().getUserOid());

            contributorBoardDTO.setOid(foundedUserLocal.getOid());
            contributorBoardDTO.setBoardId(board.get().getId());
            contributorBoardDTO.setColor(board.get().getColor());
            contributorBoardDTO.setEmoji(board.get().getEmoji());
            contributorBoardDTO.setBoardName(board.get().getName());
            contributorBoardDTO.setVisibility(board.get().getVisibility());
            contributorBoardDTO.setOwnerName(ownerOfBoard.getName());
            contributorBoardDTO.setOwnerUserName(ownerOfBoard.getUsername());
            contributorBoardDTO.setAccessRight(eachCollabsBoard.getAccessRight());
            contributorBoardDTO.setStatus(eachCollabsBoard.getStatus());
            contributorBoardDTO.setAddedOn(eachCollabsBoard.getAddedOn());

            collabsBoardDTOs.add(contributorBoardDTO);
        }
        AllBoardDTO allBoardDTO = new AllBoardDTO();
        allBoardDTO.setPersonalBoard(new ArrayList<>(toReturnPersonalBoard));
        allBoardDTO.setCollabsBoard(collabsBoardDTOs);

        return allBoardDTO;
    }

    @Transactional
    public Board createBoard(String jwtToken, CreateBoardDTO createBoardDTO) {
        if (createBoardDTO == null) {
            throw new BadRequestException("Invalid board create body");
        }

        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(jwtPayload.getOid());

        Board newBoard = new Board();
        newBoard.setName(createBoardDTO.getName());
        newBoard.setUserOid(user.getOid());
        newBoard.setMaximumTask(10);
        newBoard.setLimitMaximumTask(true);
        newBoard.setEmoji(createBoardDTO.getEmoji());
        newBoard.setColor(createBoardDTO.getColor());
        newBoard.setVisibility(createBoardDTO.getVisibility());

        boardRepository.save(newBoard);

        NewStatusDTO noStatus = new NewStatusDTO();
        noStatus.setName("No Status");
        noStatus.setDescription("A status has not been assigned");
        noStatus.setStatusColor("#CCCCCC");

        NewStatusDTO todo = new NewStatusDTO();
        todo.setName("To Do");
        todo.setDescription("The task is included in the project");
        todo.setStatusColor("#FFA500");

        NewStatusDTO doing = new NewStatusDTO();
        doing.setName("Doing");
        doing.setDescription("The task is being worked on");
        doing.setStatusColor("#FF9A00");

        NewStatusDTO done = new NewStatusDTO();
        done.setName("Done");
        done.setDescription("The task has been completed");
        done.setStatusColor("#008000");
        statusService.insertNewStatus(newBoard.getId(), noStatus, jwtToken);
        statusService.insertNewStatus(newBoard.getId(), todo, jwtToken);
        statusService.insertNewStatus(newBoard.getId(), doing, jwtToken);
        statusService.insertNewStatus(newBoard.getId(), done, jwtToken);
        return newBoard;

    }


    public BoardDTO getBoardDetail(String boardId, String jwtToken) {
        Board board = getBoardById(boardId);

        Visibility visibility = board.getVisibility();

        User user = userService.getUserByOid(board.getUserOid());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwner(userDTO);

        if(visibility == Visibility.PUBLIC){
            return boardDTO;
        }

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner &&!isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        return boardDTO;

    }

    public boolean boardExists(String id) {
        return boardRepository.existsById(id);
    }


    public BoardVisibilityDTO changeVisibility(String boardId, BoardVisibilityDTO visibility, String jwtToken) {
        Board board = getBoardById(boardId);
        BoardAuthorizationResult authorizationResult = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (visibility == null) {
            throw new BadRequestException("Invalid visibility");
        }

        Visibility visibilityStatus = visibility.getVisibility();

        if (visibilityStatus != Visibility.PUBLIC && visibilityStatus != Visibility.PRIVATE) {
            throw new BadRequestException("Invalid visibility value");
        }

        board.setVisibility(visibilityStatus);
        Board updatedBoard = boardRepository.save(board);
        return modelMapper.map(updatedBoard, BoardVisibilityDTO.class);
    }

    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
    }

    @Transactional
    public String deleteBoard(String boardId, String jwtToken) {
        BoardAuthorizationResult authorizationResult = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        deleteBoard(boardId);
        boardRepository.deleteById(boardId);

        return "BOARD ID " + boardId + " DELETED";
    }

    @Transactional
    public void deleteBoard(String boardId) {
        List<Status> statuses = statusRepository.findByBoardId(boardId);
        for (Status status : statuses) {
            taskRepository.deleteByStatusId(status.getId());
        }
        statusRepository.deleteByBoardId(boardId);
        boardRepository.deleteById(boardId);
    }

    public BoardAuthorizationResult authorizeBoardModifyAccess(String boardId, String jwtToken) {
        Board board = getBoardById(boardId);

        User user = userService.getUserByOid(board.getUserOid());
        Visibility visibilityByBoardId = boardRepository.findVisibilityByBoardId(boardId);
        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);
        boolean isPublic = (visibilityByBoardId == Visibility.PUBLIC);

        return new BoardAuthorizationResult(isOwner, isPublic);
    }

    private boolean isBoardOwner(String userOid, String jwtToken) {
        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(userOid);
        return user.getOid().equals(jwtPayload.getOid());
    }
    public boolean isCollaborator(String jwtToken, String boardId){
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
        return collaborator!=null;
    }

}
