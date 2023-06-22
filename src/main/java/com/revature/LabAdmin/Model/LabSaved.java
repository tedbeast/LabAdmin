package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Entity represents the saved state of a user's lab
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class LabSaved {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Timestamp lastUpdated;
//    this has been denormalized because writes will never happen to this value, but it's needed to locate the blob
    private String name;
    @ManyToOne
    @JoinColumn(name = "pkey_fk")
    private ProductKey productKey;
    public LabSaved(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
