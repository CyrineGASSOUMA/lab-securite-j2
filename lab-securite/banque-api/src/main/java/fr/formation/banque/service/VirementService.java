package fr.formation.banque.service;

import fr.formation.banque.dto.BanqueMapper;
import fr.formation.banque.dto.VirementRequest;
import fr.formation.banque.dto.VirementResponse;
import fr.formation.banque.entity.CompteEntity;
import fr.formation.banque.entity.VirementEntity;
import fr.formation.banque.exception.CompteIntrouvableException;
import fr.formation.banque.exception.SoldeInsuffisantException;
import fr.formation.banque.repository.CompteRepository;
import fr.formation.banque.repository.VirementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VirementService {

    private final CompteRepository compteRepository;
    private final VirementRepository virements;

    public VirementService(CompteRepository compteRepository, VirementRepository virements) {
        this.compteRepository = compteRepository;
        this.virements = virements;
    }

    public VirementResponse executer(VirementRequest requete) {
        CompteEntity source = compteRepository.findById(requete.compteSource())
                .orElseThrow(() -> new CompteIntrouvableException(requete.compteSource()));
        CompteEntity destination = compteRepository.findById(requete.compteDestination())
                .orElseThrow(() -> new CompteIntrouvableException(requete.compteDestination()));

        if (source.getSolde().compareTo(requete.montant()) < 0) {
            throw new SoldeInsuffisantException(source.getId());
        }
        source.debiter(requete.montant());
        destination.crediter(requete.montant());

        VirementEntity virement = virements.save(new VirementEntity(source.getId(), destination.getId(),
                requete.montant(), requete.libelle()));
        return BanqueMapper.versReponse(virement);
    }
}
