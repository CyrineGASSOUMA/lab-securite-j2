package fr.formation.banque.dto;

import fr.formation.banque.entity.CompteEntity;
import fr.formation.banque.entity.VirementEntity;

/** Traduction entre le contrat public (DTO) et le modèle de persistance (entités). */
public final class BanqueMapper {

    private BanqueMapper() {
    }

    public static CompteEntity versEntite(CompteRequest requete, String proprietaire) {
        return new CompteEntity(requete.titulaire(), proprietaire, requete.type());
    }

    public static CompteResponse versReponse(CompteEntity entity) {
        return new CompteResponse(entity.getId(), entity.getTitulaire(), entity.getProprietaire(),
                entity.getType(), entity.getSolde());
    }

    public static VirementResponse versReponse(VirementEntity entity) {
        return new VirementResponse(entity.getId(), entity.getCompteSource(), entity.getCompteDestination(),
                entity.getMontant(), entity.getLibelle(), entity.getDateExecution());
    }
}
