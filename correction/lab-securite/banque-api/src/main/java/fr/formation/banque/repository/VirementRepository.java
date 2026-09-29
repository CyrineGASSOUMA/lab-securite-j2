package fr.formation.banque.repository;

import fr.formation.banque.entity.VirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirementRepository extends JpaRepository<VirementEntity, Long> {
}
