package com.revature.LabAdmin.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Entity represents an active user on the site
 */
@Entity
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductKey {
    @Id
    public long productKey;
    public boolean active;
    public boolean admin;
    public boolean superAdmin;
    public int batchId;
//    might use this later
    public String name;
//    might develop some way to give a productkey access only to some lab canonicals, or assgn canonicals
//    to a unit and provide access to a unit.

    public ProductKey(long productKey, boolean active, boolean admin, boolean superAdmin) {
        this.productKey = productKey;
        this.active = active;
        this.admin = admin;
        this.superAdmin = superAdmin;
    }

    @OneToMany
    @JoinColumn(name = "pkey_fk")
    List<LabSaved> labs;
}
