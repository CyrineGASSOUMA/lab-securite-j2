package fr.formation.banque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VirementRequest(
        @NotNull Long compteSource,
        @NotNull Long compteDestination,
        @NotNull @DecimalMin("0.01") BigDecimal montant,
        @Size(max = 140) String libelle) {
}
