package ssi1.integrated.project_board.board;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi1.integrated.project_board.board.Board;

public interface BoardRepository extends JpaRepository<Board,Integer> {
}
