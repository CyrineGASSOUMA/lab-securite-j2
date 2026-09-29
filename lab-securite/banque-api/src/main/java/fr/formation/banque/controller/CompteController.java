package fr.formation.banque.controller;

import fr.formation.banque.dto.CompteRequest;
import fr.formation.banque.dto.CompteResponse;
import fr.formation.banque.entity.TypeCompte;
import fr.formation.banque.service.CompteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Couche HTTP : reçoit la requête, délègue au service, construit la réponse. Aucune règle métier ici. */
@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService service;

    public CompteController(CompteService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompteResponse> lister(@RequestParam(required = false) TypeCompte type) {
        return service.lister(type);
    }

    @GetMapping("/{id}")
    public CompteResponse lire(@PathVariable Long id) {
        return service.lire(id);
    }

    @PostMapping
    public ResponseEntity<CompteResponse> creer(@Valid @RequestBody CompteRequest requete) {
        CompteResponse compte = service.creer(requete);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(compte.id()).toUri();
        return ResponseEntity.created(location).body(compte);
    }

    @PutMapping("/{id}")
    public CompteResponse modifier(@PathVariable Long id, @Valid @RequestBody CompteRequest requete) {
        return service.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
