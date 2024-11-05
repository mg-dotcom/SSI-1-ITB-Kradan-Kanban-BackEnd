package ssi1.integrated.project_board.task;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByStatusId(Integer statusId);

    @Query("SELECT t FROM Task t WHERE t.status.name IN :filterStatuses AND t.board.id = :boardId")
    List<Task> findByStatusId(Sort sortBy, List<String> filterStatuses, String boardId);

    @Query("SELECT t FROM Task t WHERE t.board.id IN :boardId")
    List<Task> getAllSortBy(Sort sortBy, String boardId);

    @Query("SELECT t FROM Task t WHERE t.board.id IN :boardId")
    List<Task> findByBoard_Id(String boardId);

    @Query("SELECT t FROM Task t WHERE t.status.id = :statusId AND t.board.id = :boardId")
    List<Task> findByStatusIdAndBoardId(Integer statusId, String boardId);

    Task findByIdAndBoardId(Integer taskId, String boardId);

    @Query("SELECT COUNT(tf) FROM TaskFile tf WHERE tf.task.id = ?1")
    Integer countFilesByTaskId(Integer taskId);

    void deleteByStatusId(Integer statusId);

    // Method to check if a file with a given name exists for a specific task
    @Query("SELECT COUNT(tf) > 0 FROM TaskFile tf WHERE tf.task.id = :taskId AND tf.fileName = :fileName")
    boolean existsByTaskIdAndFileName(@Param("taskId") Integer taskId, @Param("fileName") String fileName);
}
