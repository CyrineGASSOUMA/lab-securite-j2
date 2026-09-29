package fr.formation.banque.dto;

import fr.formation.banque.entity.TypeCompte;

import java.math.BigDecimal;

public record CompteResponse(Long id, String titulaire, String proprietaire, TypeCompte type, BigDecimal solde) {
}
