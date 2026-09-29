package fr.formation.banque.service;

import fr.formation.banque.dto.BanqueMapper;
import fr.formation.banque.dto.CompteRequest;
import fr.formation.banque.dto.CompteResponse;
import fr.formation.banque.entity.CompteEntity;
import fr.formation.banque.entity.TypeCompte;
import fr.formation.banque.exception.CompteIntrouvableException;
import fr.formation.banque.repository.CompteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompteService {

    private final CompteRepository repository;

    public CompteService(CompteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> lister(TypeCompte type) {
        List<CompteEntity> comptes = type == null ? repository.findAll() : repository.findByType(type);
        return comptes.stream().map(BanqueMapper::versReponse).toList();
    }

    @Transactional(readOnly = true)
    public CompteResponse lire(Long id) {
        return BanqueMapper.versReponse(charger(id));
    }

    public CompteResponse creer(CompteRequest requete) {
        CompteEntity compte = repository.save(BanqueMapper.versEntite(requete));
        return BanqueMapper.versReponse(compte);
    }

    public CompteResponse modifier(Long id, CompteRequest requete) {
        CompteEntity compte = charger(id);
        compte.setTitulaire(requete.titulaire());
        compte.setType(requete.type());
        return BanqueMapper.versReponse(compte);
    }

    public void supprimer(Long id) {
        repository.delete(charger(id));
    }

    private CompteEntity charger(Long id) {
        return repository.findById(id).orElseThrow(() -> new CompteIntrouvableException(id));
    }
}
