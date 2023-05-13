package Application.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

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
}
