package com.revature.LabAdmin.Model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class BaseURL {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String url;
}
