package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi1.integrated.entities.Status;

public interface StatusRepository extends JpaRepository<Status,Integer> {
}
