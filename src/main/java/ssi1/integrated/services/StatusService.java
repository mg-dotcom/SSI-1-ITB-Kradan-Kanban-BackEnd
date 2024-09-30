package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.handler.StatusNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    public List<Status> getAllStatus(String boardId, String jwtToken) {
        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);

        // Can't access board
        if (!authorizationResult.isOwner() && !authorizationResult.isPublic()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return statusRepository.findByBoardId(boardId, sort);
    }


    public Status getStatusById(String boardId, Integer statusId, String jwtToken) {
        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);

        // Can't access board
        if (!authorizationResult.isOwner() && !authorizationResult.isPublic()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        return getAllStatus(boardId, jwtToken).stream()
                .filter(status -> status.getId().equals(statusId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));
    }

    @Transactional
    public NewStatusDTO updateStatus(String boardId, Integer statusId, NewStatusDTO updateStatusDTO, String jwtToken) {
        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if(updateStatusDTO==null){
            throw new BadRequestException("Invalid NewStatusDTO value");
        }
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (statusId.equals(1)||statusId.equals(4)) {
            throw new BadRequestException("This status cannot be modified.");
        }
        Status toUpdateStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));

        if (updateStatusDTO.getStatusColor() == null || updateStatusDTO.getStatusColor().isEmpty()) {
            toUpdateStatus.setStatusColor("#CCCCCC");
        } else {
            toUpdateStatus.setStatusColor(updateStatusDTO.getStatusColor());
        }
        toUpdateStatus.setName(updateStatusDTO.getName());
        toUpdateStatus.setDescription(updateStatusDTO.getDescription());
        Status updatedStatus = statusRepository.save(toUpdateStatus);
        return modelMapper.map(updatedStatus, NewStatusDTO.class);
    }

    @Transactional
    public NewStatusDTO insertNewStatus(String boardId, NewStatusDTO newStatusDTO, String jwtToken) {
        if(newStatusDTO==null){
            throw new BadRequestException("Invalid NewStatusDTO value");
        }
        
        boolean existStatus = getAllStatus(boardId,jwtToken).stream().anyMatch(status -> status.getName().equals(newStatusDTO.getName()));
        if (existStatus) {
            throw new BadRequestException("Status name must be unique");
        }

        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Status status = modelMapper.map(newStatusDTO, Status.class);
        status.setBoard(boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        ));
        Status insertedStatus = statusRepository.save(status);

        return modelMapper.map(insertedStatus, NewStatusDTO.class);
    }

    @Transactional
    public Status deleteStatus(String boardId,Integer statusId, String jwtToken) {
        Status toDeleteStatus = statusRepository.findById(statusId).orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + statusId));
        if (statusId.equals(1)||statusId.equals(4)) {
            throw new BadRequestException(toDeleteStatus.getName() + " cannot be delete.");
        }

        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        //validate status Id
        List<Task> taskList = taskRepository.findByStatusIdAndBoardId(statusId, boardId);

        if (taskList.isEmpty()) {
            statusRepository.delete(toDeleteStatus);
            return toDeleteStatus;
        } else {
            transferStatus(boardId, statusId, null,jwtToken);
        }

        return toDeleteStatus;
    }

    @Transactional
    public Status transferStatus(String boardId, Integer oldStatusId, Integer newStatusId, String jwtToken) {
        Status transferStatus = statusRepository.findById(newStatusId).orElseThrow(
                () -> new ItemNotFoundException("The specified status for task transfer does not exist"));
        if (oldStatusId.equals(newStatusId)) {
            throw new BadRequestException("Destination status for task transfer must be different from current status.");
        }
        List<Task> taskList = taskRepository.findByStatusIdAndBoardId(oldStatusId, boardId);

        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        for (Task task : taskList) {
            task.setStatus(transferStatus);
            taskRepository.save(task);
        }
        deleteStatus(boardId,oldStatusId,jwtToken);
        return transferStatus;
    }

    public BoardAuthorizationResult authorizeBoardReadAccess(String boardId, String jwtToken) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));
        // If the board is public, return immediately allowing access
        if (board.getVisibility() == Visibility.PUBLIC) {
            return new BoardAuthorizationResult(false, true);  // Public board, ownership doesn't matter
        }

        User user = userService.getUserByOid(board.getUserOid());

        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);

        // Private board means isPublic should be false
        return new BoardAuthorizationResult(isOwner, false);
    }

    public BoardAuthorizationResult authorizeBoardModifyAccess(String boardId, String jwtToken) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));
        User user = userService.getUserByOid(board.getUserOid());
        Visibility visibilityByBoardId = boardRepository.findVisibilityByBoardId(boardId);
        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);
        boolean isPublic = (visibilityByBoardId == Visibility.PUBLIC);

        return new BoardAuthorizationResult(isOwner, isPublic);
    }
}
