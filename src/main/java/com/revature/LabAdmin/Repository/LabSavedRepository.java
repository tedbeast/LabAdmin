package com.revature.LabAdmin.Repository;

import com.revature.LabAdmin.Model.LabSaved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabSavedRepository extends JpaRepository<LabSaved, Long> {
/**     query annotation written to get a specific lab for a specific user.
        apparently, JPA doesn't provide a super convenient way to query by a fkey.....
 **/
    @Query("select ls from LabSaved ls where ls.productKey.productKey = :pkey and ls.name = :name")
    LabSaved getSpecificSavedLab(@Param("pkey") long pkey, @Param("name") String name);
}
