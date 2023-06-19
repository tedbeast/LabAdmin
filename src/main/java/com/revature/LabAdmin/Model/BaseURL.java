package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class BaseURL {
    @Id
    long id;
    String url;
}
