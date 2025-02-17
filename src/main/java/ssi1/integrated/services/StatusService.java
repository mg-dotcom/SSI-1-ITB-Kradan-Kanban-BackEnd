package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.EditLimitDTO;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.security.JwtPayload;
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
    @Autowired
    private CollabBoardRepository collabBoardRepository;

    public List<Status> getAllStatus(String boardId, String accessToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        if (visibility == Visibility.PUBLIC) {
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            return statusRepository.findByBoardId(boardId, sort);
        }

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);


        if (visibility == Visibility.PRIVATE && !isOwner &&!isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (isPendingAndNotOwner(jwtToken, boardId, board.getUserOid())) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return statusRepository.findByBoardId(boardId, sort);
    }


    public Status getStatusById(String boardId, Integer statusId, String accessToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        if(visibility == Visibility.PUBLIC){
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            return statusRepository.findByBoardId(boardId, sort)
                    .stream()
                    .filter(status -> status.getId().equals(statusId))
                    .findFirst()
                    .orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));
        }

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken,boardId);

        if (isPendingAndNotOwner(jwtToken, boardId, board.getUserOid())) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (visibility == Visibility.PRIVATE && !isOwner &&!isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        return getAllStatus(boardId, jwtToken).stream()
                .filter(status -> status.getId().equals(statusId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));
    }

    @Transactional
    public NewStatusDTO updateStatus(String boardId, Integer statusId, NewStatusDTO updateStatusDTO, String jwtToken) {

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);

        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());


        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken, boardId);

        if (collaborator != null && collaborator.getAccessRight() == AccessRight.READ && !isOwner) {
            throw new ForbiddenException("Only board owner and collaborators with write access can edit status.");
        }

        if (board.getVisibility() == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board is private.");
        } else if (board.getVisibility() == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can edit status.");
        }

        if (statusId.equals(1) || statusId.equals(4)) {
            throw new BadRequestException("This status cannot be modified.");
        }

        Status toUpdateStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));

        toUpdateStatus.setStatusColor(
                (updateStatusDTO.getStatusColor() == null || updateStatusDTO.getStatusColor().isEmpty())
                        ? "#CCCCCC"
                        : updateStatusDTO.getStatusColor()
        );
        toUpdateStatus.setName(updateStatusDTO.getName());
        toUpdateStatus.setDescription(updateStatusDTO.getDescription());

        Status updatedStatus = statusRepository.save(toUpdateStatus);
        return modelMapper.map(updatedStatus, NewStatusDTO.class);
    }


    @Transactional
    public NewStatusDTO insertNewStatus(String boardId, NewStatusDTO newStatusDTO, String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);
        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private.");
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can add status.");
        }

        if (newStatusDTO == null) {
            throw new BadRequestException("Invalid NewStatusDTO value");
        }

        boolean existStatus = getAllStatus(boardId, jwtToken).stream().anyMatch(status -> status.getName().equals(newStatusDTO.getName()));
        if (existStatus) {
            throw new BadRequestException("Status name must be unique");
        }

        if (!isOwner && !isCollaboratorWrite) {
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
    public Status deleteStatus(String boardId, Integer statusId, String jwtToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);
        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private.");
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can delete status.");
        }

        Status toDeleteStatus = statusRepository.findById(statusId).orElseThrow(() -> new ItemNotFoundException("Status not found with STATUS ID: " + statusId));
        if (statusId.equals(1) || statusId.equals(4)) {
            throw new BadRequestException(toDeleteStatus.getName() + " cannot be delete.");
        }

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        List<Task> taskList = taskRepository.findByStatusIdAndBoardId(statusId, boardId);

        if (taskList.isEmpty()) {
            statusRepository.delete(toDeleteStatus);
            return toDeleteStatus;
        } else {
            transferStatus(boardId, statusId, null, jwtToken);
        }

        return toDeleteStatus;
    }

    @Transactional
    public Status transferStatus(String boardId, Integer oldStatusId, Integer newStatusId, String jwtToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private.");
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can delete status.");
        }

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Status transferStatus = statusRepository.findById(newStatusId).orElseThrow(
                () -> new ItemNotFoundException("The specified status for task transfer does not exist"));
        if (oldStatusId.equals(newStatusId)) {
            throw new BadRequestException("Destination status for task transfer must be different from current status.");
        }
        List<Task> taskList = taskRepository.findByStatusIdAndBoardId(oldStatusId, boardId);

        for (Task task : taskList) {
            task.setStatus(transferStatus);
            taskRepository.save(task);
        }
        deleteStatus(boardId, oldStatusId, jwtToken);
        return transferStatus;
    }

    @Transactional
    public boolean updateMaximumTask(String boardId, EditLimitDTO editLimitDTO, String jwtToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken,boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private.");
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can delete status.");
        }

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new AuthenticationException("JWT token is required") {
            };
        }
        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        if (editLimitDTO.getLimitMaximumTask() != null) {
            board.setLimitMaximumTask(editLimitDTO.getLimitMaximumTask());
            boardRepository.save(board);

            if (editLimitDTO.getLimitMaximumTask()) {
                board.setMaximumTask(10);
                boardRepository.save(board);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isBoardOwner(String userOid, String jwtToken) {
        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(userOid);

        return user.getOid().equals(jwtPayload.getOid());
    }

    public boolean isCollaboratorWriteAccess(String jwtToken, String boardId) {
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());

        return collaborator != null && collaborator.getAccessRight() == AccessRight.WRITE && collaborator.getStatus() == ssi1.integrated.project_board.collab_management.Status.ACTIVE;
    }

    public boolean isCollaborator(String jwtToken, String boardId){
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
        return collaborator!=null;
    }

    public boolean isPendingAndNotOwner(String jwtToken, String boardId, String userOid) {
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);

        User user = userService.getUserByOid(userOid);

        if (user.getOid().equals(jwtPayload.getOid())) {
            return false;
        }

        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());

        return collaborator != null
                && collaborator.getStatus() == ssi1.integrated.project_board.collab_management.Status.PENDING
                && !user.getOid().equals(jwtPayload.getOid());
    }

    private void authorizeBoardAccess(String boardId, String jwtToken, boolean requireWriteAccess) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        Visibility visibility = board.getVisibility();
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken, boardId);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && (!isCollaborator || (requireWriteAccess && !isCollaboratorWrite))) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (visibility == Visibility.PUBLIC && requireWriteAccess && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can modify this board.");
        }
    }

}
