package fr.formation.banque.controller;

import fr.formation.banque.dto.VirementRequest;
import fr.formation.banque.dto.VirementResponse;
import fr.formation.banque.service.VirementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/virements")
public class VirementController {

    private final VirementService service;

    public VirementController(VirementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VirementResponse executer(@Valid @RequestBody VirementRequest requete, @AuthenticationPrincipal Jwt jwt) {
        return service.executer(requete, jwt.getSubject());
    }
}
