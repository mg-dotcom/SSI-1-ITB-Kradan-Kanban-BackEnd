package ssi1.integrated.board.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssi1.integrated.board.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findByStatusId(Integer statusId);
    @Query("SELECT t FROM Task t WHERE t.status.name IN :filterStatuses")
    List<Task> findByStatusId(Sort sortBy,List<String> filterStatuses);

    List<Task> getAllBy(Sort sortBy);

}
