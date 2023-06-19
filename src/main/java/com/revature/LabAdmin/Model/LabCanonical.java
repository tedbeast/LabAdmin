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
