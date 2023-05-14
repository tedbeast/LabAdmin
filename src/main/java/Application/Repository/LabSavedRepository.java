package Application.Repository;

import Application.Model.LabCanonical;
import Application.Model.LabSaved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabSavedRepository extends JpaRepository<LabSaved, Long> {
    @Query("select ls from LabSaved ls where ls.productKey.productKey = :pkey and ls.canonical.name = :name")
    LabSaved getSpecificSavedLab(@Param("pkey") long pkey, @Param("name") String name);
}
