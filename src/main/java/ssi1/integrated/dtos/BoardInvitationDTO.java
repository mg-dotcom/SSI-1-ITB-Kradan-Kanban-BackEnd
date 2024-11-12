package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardInvitationDTO {
        private String accessRight;
        private String invitationStatus;
        private String ownerName;
        private String boardName;
}