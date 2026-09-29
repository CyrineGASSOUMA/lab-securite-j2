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
    public List<CompteResponse> lister(TypeCompte type, String utilisateur) {
        List<CompteEntity> comptes = type == null
                ? repository.findByProprietaire(utilisateur)
                : repository.findByProprietaireAndType(utilisateur, type);
        return comptes.stream().map(BanqueMapper::versReponse).toList();
    }

    @Transactional(readOnly = true)
    public CompteResponse lire(Long id, String utilisateur) {
        return BanqueMapper.versReponse(chargerPourProprietaire(id, utilisateur));
    }

    public CompteResponse creer(CompteRequest requete, String utilisateur) {
        CompteEntity compte = repository.save(BanqueMapper.versEntite(requete, utilisateur));
        return BanqueMapper.versReponse(compte);
    }

    public CompteResponse modifier(Long id, CompteRequest requete, String utilisateur) {
        CompteEntity compte = chargerPourProprietaire(id, utilisateur);
        compte.setTitulaire(requete.titulaire());
        compte.setType(requete.type());
        return BanqueMapper.versReponse(compte);
    }

    public void supprimer(Long id, String utilisateur) {
        repository.delete(chargerPourProprietaire(id, utilisateur));
    }

    /**
     * Correction BOLA (OWASP API1) : le compte doit appartenir à l'utilisateur du token.
     * 404 et non 403 : on ne confirme pas l'existence d'un compte qui n'est pas le sien.
     */
    public CompteEntity chargerPourProprietaire(Long id, String utilisateur) {
        CompteEntity compte = repository.findById(id).orElseThrow(() -> new CompteIntrouvableException(id));
        if (!compte.getProprietaire().equals(utilisateur)) {
            throw new CompteIntrouvableException(id);
        }
        return compte;
    }
}
