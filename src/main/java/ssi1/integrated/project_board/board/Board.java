package ssi1.integrated.project_board.board;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_ALPHABET;

@Getter
@Setter
@ToString
@Entity
@Table(name = "board",schema = "integrated2")
public class Board {
    @Id
    private String id;
    private String name;
    private String userOid;
    private Boolean limitMaximumTask;
    private Integer maximumTask;

    @PrePersist
    public void generateId() {
        Random random = new Random();
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId(random, DEFAULT_ALPHABET, 10);
        }
    }
}
