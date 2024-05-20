package ssi1.integrated.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findByStatusId(Integer statusId);
    @Query("SELECT t FROM Task t WHERE t.status.id IN :filterStatuses")
    List<Task> findByStatusId(Sort sortBy,List<Integer> filterStatuses);

    List<Task> getAllBy(Sort sortBy);

}
