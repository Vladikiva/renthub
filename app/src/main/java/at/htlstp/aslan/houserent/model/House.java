package at.htlstp.aslan.houserent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "house")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class House implements Serializable {

    @Id
    @Column(name = "house_nr")
    @NotBlank(message = "{notBlank}")
    @EqualsAndHashCode.Include
    private String houseNr;

    @NotNull(message = "{notNull}")
    @Range(min = 18500, max = 2090000, message = "{PriceError}")
    @Column(name = "price")
    private Integer price;

    @NotNull(message = "{notBlank}")
    @Column(name = "street")
    private String street;

    @NotBlank(message = "{notBlank}")
    @Column(name = "model")
    private String model;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "image_url")
    private String imageUrl;

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr == null ? null : houseNr.strip();
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.strip();
    }

    @Override
    public String toString() {
        return "(" + houseNr + ") " + model;
    }
}
