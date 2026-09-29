package fr.formation.banque.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record VirementResponse(Long id, Long compteSource, Long compteDestination, BigDecimal montant,
                               String libelle, Instant dateExecution) {
}
