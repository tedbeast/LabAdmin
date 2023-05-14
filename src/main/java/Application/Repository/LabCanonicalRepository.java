package Application.Repository;

import Application.Model.LabCanonical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LabCanonicalRepository extends JpaRepository<LabCanonical, Long> {
    public LabCanonical findByName(String name);
}
