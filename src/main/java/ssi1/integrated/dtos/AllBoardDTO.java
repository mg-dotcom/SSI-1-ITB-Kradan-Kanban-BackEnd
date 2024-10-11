package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.board.Board;

import java.util.ArrayList;

@Getter
@Setter
public class AllBoardDTO {
    private ArrayList<Board> personalBoard;
    private ArrayList<ContributorBoardDTO> CollabsBoard;
}
