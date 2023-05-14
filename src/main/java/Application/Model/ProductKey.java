package Application.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

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
