package at.htlstp.aslan.houserent.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles("local")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminApiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/admin/running-rentals")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeesMayNotUseTheAdminApi() throws Exception {
        mockMvc.perform(get("/admin/running-rentals")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSeesRunningRentals() throws Exception {
        mockMvc.perform(get("/admin/running-rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].house.houseNr").value("Помещение 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidCustomerIsRejectedWithFieldErrors() throws Exception {
        mockMvc.perform(post("/admin/create-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerNumber\": 1, \"firstName\": \"\", \"lastName\": \"Иванов\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerNumber").exists());
    }

    @Test
    void healthProbesArePublic() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
    }

    @Test
    void prometheusEndpointExposesBusinessMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("renthub_rentals_running")));
    }
}
