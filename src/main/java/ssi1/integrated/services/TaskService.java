package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ssi1.integrated.configs.ListMapper;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;

import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.statusSetting.StatusSetting;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.handler.LimitationException;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.statusSetting.StatusSettingRepository;
import ssi1.integrated.project_board.task.TaskRepository;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    //    @Autowired
//    private StatusSettingRepository statusSettingRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

        public List<TaskDTO> getAllTasks(String sortBy, List<String> filterStatuses, String direction,String boardId) {

        Sort.Order sortOrder = new Sort.Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Sort sort = Sort.by(sortOrder);

        if (filterStatuses == null) {
            List<Task> allTaskSorted= taskRepository.getAllSortBy(sort,boardId);
            return listMapper.mapList(allTaskSorted, TaskDTO.class);
        }

        return listMapper.mapList(taskRepository.findByStatusId(sort, filterStatuses,boardId), TaskDTO.class);

    }
//    public List<TaskDTO> getAllTask(String boardId) {
//        List<Task> allTasks=taskRepository.findByBoard_Id(boardId);
//        return listMapper.mapList(allTasks, TaskDTO.class);
//    }


    public Task getTaskById(Integer taskId,String boardId) {

        return taskRepository.findByStatusIdAndBoardId(taskId,boardId);

    }

//    @Transactional
//    public GeneralTaskDTO insertNewTask(NewTaskDTO newTask) {
//        StatusSetting statusSetting = statusSettingRepository.findById(1).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND THIS KANBAN ID")
//        );
//
//        Status status = statusRepository.findById(newTask.getStatus())
//                .orElseThrow(() -> new BadRequestException("status does not exist"));
//
//        if (statusSetting.getLimitMaximumTask() && !"No Status".equals(status.getName())
//                && !"Done".equals(status.getName())) {
//            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
//            if (noOfTasks >= statusSetting.getMaximumTask()) {
//                throw new LimitationException("the status has reached the limit");
//            }
//        }
//
//        Task task = modelMapper.map(newTask, Task.class);
//        task.setStatus(status);
//        Task insertedTask = taskRepository.save(task);
//        return modelMapper.map(insertedTask, GeneralTaskDTO.class);
//
//    }

//    @Transactional
//    public NewTaskDTO updateTask(Integer taskId, NewTaskDTO inputTask) {
//        StatusSetting statusSetting = statusSettingRepository.findById(1).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND THIS KANBAN ID")
//        );
//        Boolean isExistingTask = taskRepository.existsById(taskId);
//        if (!isExistingTask) {
//            throw new ItemNotFoundException("NOT FOUND");
//        }
//        Status status = statusRepository.findById(inputTask.getStatus())
//                .orElseThrow(() -> new BadRequestException("status does not exist"));
//        if(statusSetting.getLimitMaximumTask() && !"No Status".equals(status.getName())
//                && !"Done".equals(status.getName())){
//            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
//            if (noOfTasks >= statusSetting.getMaximumTask()) {
//                throw new LimitationException("the status has reached the limit");
//            }
//        }
//        Task task = modelMapper.map(inputTask, Task.class);
//        task.setStatus(status);
//        task.setId(taskId);
//        Task updatedTask = taskRepository.save(task);
//        NewTaskDTO updatedTaskDTO = new NewTaskDTO();
//        updatedTaskDTO.setId(updatedTask.getId());
//        updatedTaskDTO.setDescription(updatedTask.getDescription());
//        updatedTaskDTO.setTitle(updatedTask.getTitle());
//        updatedTaskDTO.setAssignees(updatedTask.getAssignees());
//        updatedTaskDTO.setStatus(updatedTask.getStatus().getId());
//        return updatedTaskDTO;
//    }

//    @Transactional
//    public TaskDTO removeTask(Integer taskId) {
//        Boolean isExistingTask = taskRepository.existsById(taskId);
//        if (!isExistingTask) {
//            throw new ItemNotFoundException("NOT FOUND");
//        }
//        Task task = getTaskById(taskId);
//        TaskDTO deletedTask = modelMapper.map(task, TaskDTO.class);
//        taskRepository.delete(task);
//        return deletedTask;
//    }

}
