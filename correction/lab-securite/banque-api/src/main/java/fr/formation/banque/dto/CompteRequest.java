package fr.formation.banque.dto;

import fr.formation.banque.entity.TypeCompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Le propriétaire n'est plus dans le body : il vient du token (étape 5). */
public record CompteRequest(
        @NotBlank @Size(max = 100) String titulaire,
        @NotNull TypeCompte type) {
}
