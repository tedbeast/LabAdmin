package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.*;

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
    private long id;
    private String name;
    private String commitHash;
    private String source;

    public LabCanonical(String name, String commitHash, String source) {
        this.name = name;
        this.commitHash = commitHash;
        this.source = source;
    }
}
