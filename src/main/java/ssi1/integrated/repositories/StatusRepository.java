package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;
import ssi1.integrated.entities.Status;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Status findByName(String statusName);

    Status deleteByNameAndIdNot(String name, Integer id);
}


