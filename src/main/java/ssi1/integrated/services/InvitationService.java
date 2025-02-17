package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.AccessRightDTO;
import ssi1.integrated.dtos.BoardInvitationDTO;
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
import ssi1.integrated.user_account.UserRepository;

@Service
@AllArgsConstructor
public class InvitationService {
    private BoardRepository boardRepository;
    private CollabBoardRepository collabBoardRepository;
    private JwtService jwtService;
    private CollabBoardService collabBoardService;
    private UserRepository userRepository;

    @Transactional
    public CollabBoard invitationCollab(String accessToken, String boardId, InvitationDTO invitationDTO){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        JwtPayload jwtPayload=jwtService.extractPayload(accessToken);

        CollabBoard collaborator=collabBoardRepository.findByBoard_IdAndUser_Oid(board.getId(),jwtPayload.getOid());
        if(collaborator==null){
            throw new ItemNotFoundException("The "+jwtPayload.getOid()+" is not a collaborator on the board.");
        }
        if (!invitationDTO.getCollabStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {
            collabBoardService.deleteCollaborator(accessToken, boardId, collaborator.getUser().getOid());
            return collaborator;
        }

        try {
            collaborator.setStatus(Status.valueOf(invitationDTO.getCollabStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Status provided.");
        }

        return collaborator;
    }

    public BoardInvitationDTO getInvitaionStatus(String accessToken, String boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        JwtPayload jwtPayload=jwtService.extractPayload(accessToken);
        User owner = userRepository.findByOid(board.getUserOid());

        CollabBoard collaborator=collabBoardRepository.findByBoard_IdAndUser_Oid(board.getId(),jwtPayload.getOid());
        if(collaborator==null){
            throw new ItemNotFoundException("The "+jwtPayload.getOid()+" is not a collaborator on the board.");
        }
        BoardInvitationDTO boardInvitationDTO = new BoardInvitationDTO();
        boardInvitationDTO.setAccessRight(collaborator.getAccessRight().toString());
        boardInvitationDTO.setInvitationStatus(collaborator.getStatus().toString());
        boardInvitationDTO.setOwnerName(owner.getName());
        boardInvitationDTO.setBoardName(board.getName());
        return boardInvitationDTO;
    }

}
