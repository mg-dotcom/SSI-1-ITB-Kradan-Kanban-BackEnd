package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.user_account.UserDTO;

@Getter
@Setter
public class BoardDTO {
    private Integer id;
    private String name;
    private UserDTO owner;
}
