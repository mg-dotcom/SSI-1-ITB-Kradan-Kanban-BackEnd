package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;;
import ssi1.integrated.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {

}
