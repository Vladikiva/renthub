package at.htlstp.aslan.houserent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "rental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Rental implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @FutureOrPresent(message = "{futOrPres}")
    @NotNull(message = "{notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "rental_date")
    private LocalDate rentalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{futOrPres}")
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "km")
    private Integer km;

    @ManyToOne
    @JoinColumn(name = "driver_customer_number")
    @NotNull(message = "{notNull}")
    private Customer driver;

    @ManyToOne
    @JoinColumn(name = "house_nr")
    @NotNull(message = "{notNull}")
    private House house;

    @ManyToOne
    @JoinColumn(name = "rental_station_id")
    @NotNull(message = "{notNull}")
    private Station rentalStation;

    @ManyToOne
    @JoinColumn(name = "return_station_id")
    private Station returnStation;
}
