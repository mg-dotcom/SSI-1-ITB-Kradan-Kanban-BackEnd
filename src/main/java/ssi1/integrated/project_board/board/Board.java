package ssi1.integrated.project_board.board;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Random;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_ALPHABET;

@Getter
@Setter
@ToString
@Entity
@Table(name = "board", schema = "integrated2")
public class Board {
    @Id
    private String id;
    private String name;
    private String userOid;
    private String emoji;
    private String color;
    private Boolean limitMaximumTask;
    private Integer maximumTask;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("PRIVATE")
    private Visibility visibility;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @PrePersist
    public void generateId() {
        Random random = new Random();
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId(random, DEFAULT_ALPHABET, 10);
        }
    }
}
