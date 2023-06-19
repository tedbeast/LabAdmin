package com.revature.LabAdmin.Repository;

import com.revature.LabAdmin.Model.LabCanonical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabCanonicalRepository extends JpaRepository<LabCanonical, Long> {
    public LabCanonical findByName(String name);
}
