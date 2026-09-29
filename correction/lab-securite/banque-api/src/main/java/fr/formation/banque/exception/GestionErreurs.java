package fr.formation.banque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/** Traduit les exceptions métier en réponses HTTP au format RFC 9457. */
@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(CompteIntrouvableException.class)
    public ProblemDetail compteIntrouvable(CompteIntrouvableException e) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        probleme.setTitle("Compte introuvable");
        return probleme;
    }

    @ExceptionHandler(SoldeInsuffisantException.class)
    public ProblemDetail soldeInsuffisant(SoldeInsuffisantException e) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        probleme.setTitle("Solde insuffisant");
        return probleme;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail requeteInvalide(MethodArgumentNotValidException e) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "La requête contient des champs invalides");
        probleme.setTitle("Requête invalide");
        Map<String, String> erreurs = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> String.valueOf(f.getDefaultMessage()), (a, b) -> a));
        probleme.setProperty("erreurs", erreurs);
        return probleme;
    }
}
