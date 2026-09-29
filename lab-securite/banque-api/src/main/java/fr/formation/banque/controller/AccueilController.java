package fr.formation.banque.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Le globe de l'onglet Ports ouvre "/" : on redirige directement vers Swagger. */
@Hidden
@Controller
public class AccueilController {

    @GetMapping("/")
    public String accueil() {
        return "redirect:/swagger-ui.html";
    }
}
