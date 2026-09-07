package at.htlstp.aslan.houserent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Customer implements Serializable {

    @Id
    @Column(name = "customer_number")
    @Range(min = 111_111, max = 999_999, message = "{customerNumberRange}")
    @EqualsAndHashCode.Include
    @NotNull(message = "{notNull}")
    private Integer customerNumber;

    @Size(min = 1, max = 255, message = "{nameRange}")
    @NotNull(message = "{notNull}")
    @Column(name = "last_name")
    private String lastName;

    @Size(min = 1, max = 255, message = "{nameRange}")
    @NotNull(message = "{notNull}")
    @Column(name = "first_name")
    private String firstName;

    public void setLastName(String lastName) {
        this.lastName = lastName == null ? null : lastName.strip();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.strip();
    }

    @Override
    public String toString() {
        return "(" + customerNumber + ") " + lastName.toUpperCase() + ' ' + firstName;
    }
}
