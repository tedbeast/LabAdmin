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
    @Lob
    public byte[] zip;
    public Timestamp lastUpdated;

    public LabCanonical(String name, byte[] zip, Timestamp lastUpdated) {
        this.name = name;
        this.zip = zip;
        this.lastUpdated = lastUpdated;
    }
}
