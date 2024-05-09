package ssi1.integrated.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ssi1.integrated.entities.Status;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Status findByName(String statusName);
}

