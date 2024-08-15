<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/repositories/StatusRepository.java
package ssi1.integrated.board.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.board.entities.Status;
========
package ssi1.integrated.project_board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.project_board.Status;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/StatusRepository.java

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);
}


