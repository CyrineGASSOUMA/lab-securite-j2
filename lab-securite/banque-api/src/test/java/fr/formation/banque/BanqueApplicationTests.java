package fr.formation.banque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BanqueApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void lApiRenvoieLesQuatreComptes() throws Exception {
        mvc.perform(get("/comptes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void unCompteInconnuRenvoie404() throws Exception {
        mvc.perform(get("/comptes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Compte introuvable"));
    }
}
