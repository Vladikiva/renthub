package at.htlstp.aslan.houserent.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Renders the Thymeleaf views end to end so that template errors fail the build. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class PublicViewTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchPageRendersForAnonymousUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("RentHub")))
                .andExpect(content().string(containsString("Войти")));
    }

    @Test
    @WithMockUser(username = "emp", roles = "EMPLOYEE")
    void employeeSeesTheRentalMenu() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Создать аренду")))
                .andExpect(content().string(containsString("Привет, emp")));
    }

    @Test
    @WithMockUser(username = "emp", roles = "EMPLOYEE")
    void employeeRentalPagesRender() throws Exception {
        mockMvc.perform(get("/employee/create-rental")).andExpect(status().isOk());
        mockMvc.perform(get("/employee/running-rentals")).andExpect(status().isOk());
    }
}
