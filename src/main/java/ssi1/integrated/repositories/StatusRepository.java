package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.entities.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);
}


