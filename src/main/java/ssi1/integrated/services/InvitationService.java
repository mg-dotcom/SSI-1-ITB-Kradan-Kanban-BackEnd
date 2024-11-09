package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.AccessRightDTO;
import ssi1.integrated.dtos.CollaboratorDTO;
import ssi1.integrated.dtos.InvitationDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.collab_management.Status;
import ssi1.integrated.project_board.user_local.UserLocal;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

@Service
public class InvitationService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CollabBoardRepository collabBoardRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CollabBoardService collabBoardService;

    @Transactional
    public CollabBoard invitationCollab(String accessToken, String boardId, InvitationDTO invitationDTO){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        JwtPayload jwtPayload=jwtService.extractPayload(accessToken);

        CollabBoard collaborator=collabBoardRepository.findByBoard_IdAndUser_Oid(board.getId(),jwtPayload.getOid());
        //404
        if(collaborator==null){
            throw new ItemNotFoundException("The "+jwtPayload.getOid()+" is not a collaborator on the board.");
        }
        if (invitationDTO.getCollabStatus() != Status.ACTIVE.toString().toUpperCase()){
            collabBoardService.deleteCollaborator(accessToken,boardId,collaborator.getUser().getOid());
            return collaborator;
        }

        try {
            collaborator.setStatus(Status.valueOf(invitationDTO.getCollabStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            //400
            throw new BadRequestException("Invalid access right provided.");
        }

        return collaborator;
    }
}
