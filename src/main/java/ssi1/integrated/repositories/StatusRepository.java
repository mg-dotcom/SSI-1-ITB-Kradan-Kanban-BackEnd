package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Status findByName(String statusName);
}