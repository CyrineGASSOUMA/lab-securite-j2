# Lab J2.2 — Corrigé formateur

**Code final :** `correction/lab-securite/banque-api`. Lancer avec `mvn spring-boot:run`, dérouler `requetes.http` de haut en bas.
**Vérification automatique :** `mvn verify` exécute `SecuriteTests` (les 8 résultats ci-dessous).

## Résultats attendus

| Test | Avant | Après |
|---|---|---|
| GET /comptes sans token | 200, 4 comptes | **401** |
| Token modifié (un caractère au milieu) | — | **401** |
| Alice : GET /comptes | — | 200, **2 comptes** |
| Bob (comptes:lire) : GET /comptes/1 | 200 | **404** |
| Bob (comptes:lire) : POST /comptes | 201 | **403** |
| Alice : POST /comptes avec `"proprietaire":"bob"` | propriétaire bob | **propriétaire alice** |
| Alice : virement depuis le compte 3 | 201 | **404** |
| Alice : virement 1 → 3 | 201 | **201** |

## Réponses aux questions

- **Étape 0 :** tout le monde lit tout, vire depuis n'importe quel compte, et choisit le propriétaire.
- **WWW-Authenticate :** `Bearer`, le serveur indique le type d'authentification attendu.
- **Token modifié → 401 :** la signature HMAC ne correspond plus. On peut lire un JWT (il est encodé, pas chiffré), mais pas le modifier.
- **401 vs 403 :** 401 = qui êtes-vous ? 403 = je sais qui vous êtes, mais vous n'avez pas le droit.
- **404 plutôt que 403 :** on ne confirme pas l'existence d'un compte qui n'est pas le sien. C'est le choix courant en banque.
- **Pourquoi BOLA n'est pas dans SecuriteConfig :** la configuration connaît les URL et les scopes, pas les données. Le contrôle du propriétaire est une règle métier : il va dans le service.

## Pièges fréquents

| Symptôme | Cause | Solution |
|---|---|---|
| Erreur au démarrage sur `securite.jwt.secret` | ligne non décommentée | étape 1.4 |
| Toutes les erreurs deviennent 401 | `/error` absent du `permitAll` | déjà présent dans le fichier fourni |
| 403 avec le bon token | scope sans préfixe (`comptes:lire`) | `SCOPE_comptes:lire` |
| Token sans scope | espaces non encodés dans l'URL | `%20` entre les scopes |
| Token modifié mais toujours 200 | dernier caractère changé (bits de padding) | changer un caractère au milieu |
| Swagger : Failed to fetch | proxy Codespaces | `server.forward-headers-strategy=framework`, déjà présent |

## Par participant

- **Data (Informatica) :** insister sur les étapes 2 et 3. Son connecteur devra obtenir un token (client credentials), l'envoyer dans `Authorization`, et interpréter 401 (renouveler le token) et 403 (demander le scope). Lire un catalogue d'API = repérer les scopes exigés par endpoint.
- **iOS (BFF) :** étapes 4 et 5. Dans son BFF, le `sub` du token est la seule source fiable de l'identité : jamais un id envoyé par l'app.

## Remarque production

`TokenController` et la clé HMAC n'existent que pour la formation. En production : supprimer ces deux éléments et le bean `JwtDecoder`, puis configurer `spring.security.oauth2.resourceserver.jwt.issuer-uri`. Le reste du code (règles, contrôle BOLA) est identique.
