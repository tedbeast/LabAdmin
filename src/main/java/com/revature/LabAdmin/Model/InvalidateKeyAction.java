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
public class InvalidateKeyAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Timestamp timestamp;
    @ManyToOne
    @JoinColumn(name = "keyFK")
    private ProductKey target;
    @ManyToOne
    @JoinColumn(name = "adminFK")
    private ProductKey createdBy;
}
