package fr.formation.banque.dto;

import fr.formation.banque.entity.TypeCompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompteRequest(
        @NotBlank @Size(max = 100) String titulaire,
        @NotNull TypeCompte type,
        @NotBlank String proprietaire) {
}
