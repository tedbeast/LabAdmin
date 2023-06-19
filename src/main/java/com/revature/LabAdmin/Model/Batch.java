package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Batch {
    @Id
    private long id;
    @OneToMany
    List<ProductKey> productKeys;
    @ManyToOne
    @JoinColumn(name = "curriculaFK")
    private Curricula curricula;
}
