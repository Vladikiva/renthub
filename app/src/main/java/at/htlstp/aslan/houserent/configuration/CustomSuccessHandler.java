package at.htlstp.aslan.houserent.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        redirectStrategy.sendRedirect(request, response, determineTargetUrl(authentication));
    }

    /**
     * ADMIN lands on the REST endpoint listing running rentals, EMPLOYEE on the
     * home page, anyone else back on the login page.
     */
    protected String determineTargetUrl(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_EMPLOYEE")) {
            return "/";
        }
        if (roles.contains("ROLE_ADMIN")) {
            return "/admin/running-rentals";
        }
        return "/login";
    }
}
