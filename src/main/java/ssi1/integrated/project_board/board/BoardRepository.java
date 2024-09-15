package ssi1.integrated.project_board.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,String> {
    List<Board> findByUserOid(String userOid);
}