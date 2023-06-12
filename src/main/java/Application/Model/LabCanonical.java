package Application.Model;

import lombok.*;

import javax.persistence.*;
import java.io.File;
import java.sql.Timestamp;

/**
 * Entity represents the official "starting point" of a lab written by trainers and stored in a github repository
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class LabCanonical {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String name;
    public String commitHash;
    public String source;

    public LabCanonical(String name, String commitHash, String source) {
        this.name = name;
        this.commitHash = commitHash;
        this.source = source;
    }
}
