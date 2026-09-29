package fr.formation.banque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "compte")
public class CompteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulaire;

    /** Identifiant de connexion du client (alice, bob). */
    @Column(nullable = false)
    private String proprietaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCompte type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    protected CompteEntity() {
        // requis par JPA
    }

    public CompteEntity(String titulaire, String proprietaire, TypeCompte type) {
        this.titulaire = titulaire;
        this.proprietaire = proprietaire;
        this.type = type;
    }

    public void debiter(BigDecimal montant) {
        this.solde = solde.subtract(montant);
    }

    public void crediter(BigDecimal montant) {
        this.solde = solde.add(montant);
    }

    public Long getId() { return id; }
    public String getTitulaire() { return titulaire; }
    public String getProprietaire() { return proprietaire; }
    public TypeCompte getType() { return type; }
    public BigDecimal getSolde() { return solde; }

    public void setTitulaire(String titulaire) { this.titulaire = titulaire; }
    public void setType(TypeCompte type) { this.type = type; }
}
