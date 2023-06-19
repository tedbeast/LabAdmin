package com.revature.LabAdmin.Model;

import javax.persistence.*;

@Entity

public class LabCurriculaMapping {
    @Id
    private long id;
    private int indexWithinCurricula;
    @ManyToOne
    @JoinColumn(name = "labFK")
    private LabCanonical labCanonical;
    @ManyToOne
    @JoinColumn(name = "curriculaFK")
    private Curricula curricula;
}
