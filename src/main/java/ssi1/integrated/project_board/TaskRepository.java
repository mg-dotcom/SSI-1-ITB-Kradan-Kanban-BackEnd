<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/repositories/TaskRepository.java
package ssi1.integrated.board.repositories;
========
package ssi1.integrated.project_board;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/TaskRepository.java

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/repositories/TaskRepository.java
import ssi1.integrated.board.entities.Task;
========
import ssi1.integrated.project_board.Task;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/TaskRepository.java

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findByStatusId(Integer statusId);
    @Query("SELECT t FROM Task t WHERE t.status.name IN :filterStatuses")
    List<Task> findByStatusId(Sort sortBy,List<String> filterStatuses);

    List<Task> getAllBy(Sort sortBy);

}
