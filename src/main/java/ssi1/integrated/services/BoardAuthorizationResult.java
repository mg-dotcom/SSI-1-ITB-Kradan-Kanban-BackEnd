package ssi1.integrated.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssi1.integrated.project_board.board.Board;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardAuthorizationResult {
    private  boolean isOwner;
    private  boolean isPublic;
}
