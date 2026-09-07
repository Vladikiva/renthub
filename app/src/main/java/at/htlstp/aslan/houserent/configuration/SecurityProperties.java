package at.htlstp.aslan.houserent.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application users. Passwords are never hard coded: they are supplied through
 * environment variables such as {@code RENTHUB_SECURITY_USERS_0_PASSWORD}.
 */
@Validated
@ConfigurationProperties(prefix = "renthub.security")
public record SecurityProperties(@Valid @NotEmpty List<UserCredentials> users) {

    public record UserCredentials(@NotEmpty String username, @NotEmpty String password, @NotEmpty String role) {}
}
