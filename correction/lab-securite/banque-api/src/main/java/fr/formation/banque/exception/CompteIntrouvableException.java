package fr.formation.banque.exception;

public class CompteIntrouvableException extends RuntimeException {

    public CompteIntrouvableException(Long id) {
        super("Le compte " + id + " n'existe pas");
    }
}
