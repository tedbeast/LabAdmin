package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Batch {
    @Id
    long id;
    @OneToMany
    List<ProductKey> productKeys;
}
