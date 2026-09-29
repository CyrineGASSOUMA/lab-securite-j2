package fr.formation.banque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "virement")
public class VirementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long compteSource;

    @Column(nullable = false)
    private Long compteDestination;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    private String libelle;

    @Column(nullable = false)
    private Instant dateExecution;

    protected VirementEntity() {
        // requis par JPA
    }

    public VirementEntity(Long compteSource, Long compteDestination, BigDecimal montant, String libelle) {
        this.compteSource = compteSource;
        this.compteDestination = compteDestination;
        this.montant = montant;
        this.libelle = libelle;
        this.dateExecution = Instant.now();
    }

    public Long getId() { return id; }
    public Long getCompteSource() { return compteSource; }
    public Long getCompteDestination() { return compteDestination; }
    public BigDecimal getMontant() { return montant; }
    public String getLibelle() { return libelle; }
    public Instant getDateExecution() { return dateExecution; }
}
