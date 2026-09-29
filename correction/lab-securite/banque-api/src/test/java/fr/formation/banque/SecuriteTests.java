package fr.formation.banque;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Les tests attendus du lab, exécutés automatiquement par mvn verify. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecuriteTests {

    private static final String TOUS_LES_SCOPES = "comptes:lire comptes:ecrire virements:creer";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    private String token(String sub, String scope) throws Exception {
        String reponse = mvc.perform(post("/dev/token").param("sub", sub).param("scope", scope))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(reponse).get("access_token").asText();
    }

    @Test
    void sansToken401() throws Exception {
        mvc.perform(get("/comptes")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFalsifie401() throws Exception {
        String bearer = token("alice", TOUS_LES_SCOPES);
        int i = bearer.length() - 10;   // un caractère au milieu de la signature
        char remplace = bearer.charAt(i) == 'a' ? 'b' : 'a';
        String falsifie = bearer.substring(0, i) + remplace + bearer.substring(i + 1);
        mvc.perform(get("/comptes").header("Authorization", falsifie)).andExpect(status().isUnauthorized());
    }

    @Test
    void aliceNeVoitQueSesComptes() throws Exception {
        mvc.perform(get("/comptes").header("Authorization", token("alice", TOUS_LES_SCOPES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].proprietaire").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.is("alice"))));
    }

    @Test
    void bobNeLitPasLeCompteDAlice404() throws Exception {
        mvc.perform(get("/comptes/1").header("Authorization", token("bob", "comptes:lire")))
                .andExpect(status().isNotFound());
    }

    @Test
    void bobSansScopeEcriture403() throws Exception {
        mvc.perform(post("/comptes").header("Authorization", token("bob", "comptes:lire"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulaire\":\"Bob Durand\",\"type\":\"COURANT\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void leProprietaireVientDuToken() throws Exception {
        mvc.perform(post("/comptes").header("Authorization", token("alice", TOUS_LES_SCOPES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulaire\":\"Alice Martin\",\"type\":\"COURANT\",\"proprietaire\":\"bob\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proprietaire").value("alice"))
                .andExpect(jsonPath("$.solde").value(0));
    }

    @Test
    void virementDepuisLeCompteDeBob404() throws Exception {
        mvc.perform(post("/virements").header("Authorization", token("alice", TOUS_LES_SCOPES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compteSource\":3,\"compteDestination\":1,\"montant\":100}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void virementDAliceVersBob201() throws Exception {
        mvc.perform(post("/virements").header("Authorization", token("alice", TOUS_LES_SCOPES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compteSource\":1,\"compteDestination\":3,\"montant\":100}"))
                .andExpect(status().isCreated());
    }
}
