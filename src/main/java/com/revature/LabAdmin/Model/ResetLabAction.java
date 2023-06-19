package com.revature.LabAdmin.Model;
import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class ResetLabAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    Timestamp timestamp;
    @ManyToOne
    LabSaved target;
}
