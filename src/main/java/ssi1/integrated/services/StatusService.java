package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board_status.BoardStatus;
import ssi1.integrated.project_board.board_status.BoardStatusRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BoardStatusService boardStatusService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardStatusRepository boardStatusRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus(String boardId) {
        List<BoardStatus> foundedBoardStatus = boardStatusRepository.findByBoardId(boardId);
        System.out.println(foundedBoardStatus.toString());
        return foundedBoardStatus.stream().map(BoardStatus::getStatus).collect(Collectors.toList());
    }

    public Status getStatusById(String boardId, Integer statusId) {
        return getAllStatus(boardId).stream()
                .filter(status -> status.getId().equals(statusId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Status Not Found"));
    }

    @Transactional
    public NewStatusDTO updateStatus(String boardId, Integer statusId, NewStatusDTO updateStatusDTO) {
        boolean existStatus = getAllStatus(boardId).stream().anyMatch(status -> status.getName().equals(updateStatusDTO.getName()));
        if (existStatus) {
            throw new BadRequestException("Status name must be unique");
        }
        if (statusId.equals(1)) {
            throw new BadRequestException("This status cannot be modified.");
        }
        Status toUpdateStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found"));

        if (updateStatusDTO.getStatusColor() == null || updateStatusDTO.getStatusColor().isEmpty()) {
            toUpdateStatus.setStatusColor("#CCCCCC");
        } else {
            toUpdateStatus.setStatusColor(updateStatusDTO.getStatusColor());
        }
        toUpdateStatus.setName(updateStatusDTO.getName());
        toUpdateStatus.setDescription(updateStatusDTO.getDescription());
        Status updatedStatus = statusRepository.save(toUpdateStatus);
        NewStatusDTO mappedStatus = modelMapper.map(updatedStatus, NewStatusDTO.class);
        return mappedStatus;
    }

    @Transactional
    public NewStatusDTO insertNewStatus(String boardId, NewStatusDTO newStatusDTO) {
        boolean existStatus = getAllStatus(boardId).stream().anyMatch(status -> status.getName().equals(newStatusDTO.getName()));
        if (existStatus) {
            throw new BadRequestException("Status name must be unique");
        }
        Status status = modelMapper.map(newStatusDTO, Status.class);
        Status insertedStatus = statusRepository.save(status);
        NewStatusDTO mappedStatus = modelMapper.map(insertedStatus, NewStatusDTO.class);
        boardStatusService.addStatusBoard(mappedStatus.getId(), boardId);
        return mappedStatus;
    }

    @Transactional
    public Status deleteStatus(String boardId,Integer statusId) {
        Status toDeleteStatus = statusRepository.findById(statusId).orElseThrow(() -> new BadRequestException("The specified status for delete doesn't exist"));
        if (statusId.equals(1)) {
            throw new BadRequestException(toDeleteStatus.getName() + " cannot be delete.");
        }
        Integer toDeleteBoardStatus = boardStatusRepository.findBoardStatusByBoard_IdAndStatus_Id(boardId,statusId).getId();
        boardStatusRepository.deleteById(toDeleteBoardStatus);
        statusRepository.delete(toDeleteStatus);
        return toDeleteStatus;
    }

    @Transactional
    public Status transferStatus(String boardId, Integer oldStatusId, Integer newStatusId) {
        Status transferStatus = statusRepository.findById(newStatusId).orElseThrow(
                () -> new BadRequestException("The specified status for task transfer does not exist"));
        if (oldStatusId.equals(newStatusId)) {
            throw new BadRequestException("Destination status for task transfer must be different from current status.");
        }
        List<Task> taskList = taskRepository.findByStatusIdAndBoardId(oldStatusId, boardId);

        for (Task task : taskList) {
            task.setStatus(transferStatus);
            taskRepository.save(task);
        }
        deleteStatus(boardId,oldStatusId);
        return transferStatus;
    }

}
