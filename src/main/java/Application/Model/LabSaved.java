package Application.Model;

import lombok.*;

import javax.persistence.*;
import java.io.File;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class LabSaved {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Lob
    public byte[] zip;
    public Timestamp lastUpdated;
    @ManyToOne
    @JoinColumn(name = "lab_fk")
    public LabCanonical canonical;
    @ManyToOne
    @JoinColumn(name = "pkey_fk")
    public ProductKey productKey;

    public LabSaved(byte[] zip, Timestamp lastUpdated) {
        this.zip = zip;
        this.lastUpdated = lastUpdated;
    }
}
