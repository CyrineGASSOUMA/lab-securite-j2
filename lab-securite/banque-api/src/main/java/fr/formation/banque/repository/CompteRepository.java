package fr.formation.banque.repository;

import fr.formation.banque.entity.CompteEntity;
import fr.formation.banque.entity.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompteRepository extends JpaRepository<CompteEntity, Long> {

    List<CompteEntity> findByType(TypeCompte type);
}
