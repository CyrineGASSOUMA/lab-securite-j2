package fr.formation.banque.exception;

public class SoldeInsuffisantException extends RuntimeException {

    public SoldeInsuffisantException(Long id) {
        super("Solde insuffisant sur le compte " + id);
    }
}
