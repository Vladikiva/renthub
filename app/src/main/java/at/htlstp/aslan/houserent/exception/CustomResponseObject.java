package at.htlstp.aslan.houserent.exception;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomResponseObject {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;

    public CustomResponseObject(String message) {
        this.message = message;
    }
}
