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
    long id;
    Timestamp timestamp;
    @ManyToOne
    @JoinColumn(name = "keyFK")
    ProductKey target;
    @ManyToOne
    @JoinColumn(name = "adminFK")
    ProductKey createdBy;
}
