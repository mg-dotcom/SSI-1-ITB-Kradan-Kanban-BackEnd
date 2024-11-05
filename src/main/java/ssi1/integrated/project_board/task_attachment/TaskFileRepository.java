package ssi1.integrated.project_board.task_attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskFileRepository extends JpaRepository<TaskFile, Integer> {
    List<TaskFile> findAllByTaskId(Integer taskId);
}