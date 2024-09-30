package ssi1.integrated.project_board.task;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ssi1.integrated.project_board.task.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findByStatusId(Integer statusId);
    @Query("SELECT t FROM Task t WHERE t.status.name IN :filterStatuses AND t.board.id = :boardId")
    List<Task> findByStatusId(Sort sortBy,List<String> filterStatuses,String boardId);

    @Query("SELECT t FROM Task t WHERE t.board.id IN :boardId")
    List<Task> getAllSortBy(Sort sortBy,String boardId);
    @Query("SELECT t FROM Task t WHERE t.board.id IN :boardId")
    List<Task> findByBoard_Id(String boardId);

    @Query("SELECT t FROM Task t WHERE t.status.id = :statusId AND t.board.id = :boardId")
    List<Task> findByStatusIdAndBoardId(Integer statusId,String boardId);

    Task findByIdAndBoardId(Integer taskId,String boardId);

    void deleteByStatusId(Integer statusId);




}
