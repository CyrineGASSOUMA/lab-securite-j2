# Lab J2.2 — Sécuriser une API REST

**Durée :** 1h15 · **Objectif :** partir d'une API bancaire qui fonctionne mais n'est pas protégée, puis la sécuriser pas à pas : authentification JWT, droits par scope, faille BOLA.

## L'API fournie

Une API complète et prête à lancer, organisée en couches :

| Couche | Package | Rôle |
|---|---|---|
| HTTP | `controller` | reçoit la requête, renvoie la réponse, aucune règle métier |
| Contrat public | `dto` | ce que le client envoie et reçoit (jamais les entités) |
| Métier | `service` | règles : solde suffisant, compte existant… |
| Persistance | `repository`, `entity` | accès à la base H2 |
| Erreurs | `exception` | réponses au format RFC 9457 (400, 404, 422) |

Données de départ : **alice** possède les comptes 1 et 2, **bob** les comptes 3 et 4.

## Étape 0 — Découvrir l'API (10 min)

```
cd /workspaces/restful-api-labs-j2/lab-securite/banque-api
mvn spring-boot:run
```

Swagger : onglet **Ports**, port 8080, icône globe (redirection automatique vers Swagger).
Ou `requetes.http` : bouton **Send Request** au-dessus de chaque requête.

Jouez les requêtes 1 à 8, puis répondez :
1. Qui peut lire le compte 1 d'Alice ?
2. Qui peut vider le compte 3 de Bob avec un virement ?
3. Dans la requête 4, qui décide à qui appartient le compte créé ?

## Étape 1 — Activer la sécurité (10 min)

1. Arrêtez l'API (`Ctrl+C`).
2. Dans `pom.xml`, à l'emplacement du commentaire *Étape 1*, ajoutez :
   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
   </dependency>
   ```
3. Copiez les deux fichiers fournis :
   ```
   mkdir -p src/main/java/fr/formation/banque/securite
   cp ../fichiers/*.java src/main/java/fr/formation/banque/securite/
   ```
4. Dans `application.properties`, décommentez la ligne `securite.jwt.secret=...`.
5. Relancez. `GET /comptes` doit répondre **401**.

**Question :** que dit le header `WWW-Authenticate` de la réponse ?

## Étape 2 — Obtenir et lire un token (10 min)

`TokenController` joue le rôle du serveur d'autorisation (Keycloak, Auth0, Entra ID…). Ajoutez en haut de `requetes.http` :

```
# @name alice
POST {{base}}/dev/token?sub=alice&scope=comptes:lire%20comptes:ecrire%20virements:creer

@tokenAlice = {{alice.response.body.access_token}}

###
# @name bob
POST {{base}}/dev/token?sub=bob&scope=comptes:lire

@tokenBob = {{bob.response.body.access_token}}

###
GET {{base}}/comptes
Authorization: Bearer {{tokenAlice}}
```

1. Collez le token d'Alice dans https://jwt.io : repérez `sub`, `iss`, `scope`, `exp`.
2. Modifiez **un caractère au milieu** du token, puis rejouez : **401**. Pourquoi ?
3. Dans Swagger : bouton **Authorize**, collez le token (sans `Bearer`).

## Étape 3 — Des droits par scope (15 min)

Dans `securite/SecuriteConfig.java`, remplacez le `TODO` par :

```java
.requestMatchers(HttpMethod.GET, "/comptes", "/comptes/**").hasAuthority("SCOPE_comptes:lire")
.requestMatchers("/comptes", "/comptes/**").hasAuthority("SCOPE_comptes:ecrire")
.requestMatchers(HttpMethod.POST, "/virements").hasAuthority("SCOPE_virements:creer")
```

Relancez. Avec le token de Bob (lecture seule) :
- `GET /comptes` → **200**
- `POST /comptes` → **403**

**Question :** quelle différence entre 401 et 403 ?

## Étape 4 — La faille BOLA (20 min)

Avec le token de **Bob**, lisez `GET /comptes/1`. Qu'obtenez-vous ?

**Corriger dans `CompteService` :** remplacez la méthode `charger` par :

```java
public CompteEntity chargerPourProprietaire(Long id, String utilisateur) {
    CompteEntity compte = repository.findById(id).orElseThrow(() -> new CompteIntrouvableException(id));
    if (!compte.getProprietaire().equals(utilisateur)) {
        throw new CompteIntrouvableException(id);
    }
    return compte;
}
```

Puis, dans chaque méthode du service, ajoutez un paramètre `String utilisateur` et appelez `chargerPourProprietaire(id, utilisateur)`.
La liste ne renvoie plus que les comptes de l'utilisateur : ajoutez dans `CompteRepository` :

```java
List<CompteEntity> findByProprietaire(String proprietaire);
List<CompteEntity> findByProprietaireAndType(String proprietaire, TypeCompte type);
```

**Dans `CompteController` :** chaque méthode reçoit l'utilisateur du token et le transmet au service :

```java
public CompteResponse lire(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    return service.lire(id, jwt.getSubject());
}
```

**Virements :** dans `VirementService`, le compte source doit appartenir à l'utilisateur (`chargerPourProprietaire`). La destination peut appartenir à un autre client.

Vérifiez : Bob → `GET /comptes/1` → **404**. Alice → virement depuis le compte 3 → **404**.

**Question :** pourquoi 404 et pas 403 ?

## Étape 5 — Ne jamais croire le client (10 min)

Avec le token d'Alice, créez un compte avec `"proprietaire": "bob"` dans le body. À qui appartient-il ?

Corrigez : supprimez `proprietaire` de `CompteRequest`. Le propriétaire est désormais `jwt.getSubject()`, transmis au service puis au mapper.

## Récapitulatif OWASP API Top 10

| Étape | Risque | Protection |
|---|---|---|
| 1-2 | API2 — Broken Authentication | JWT signé, vérifié à chaque requête |
| 3 | API5 — Broken Function Level Authorization | scopes par endpoint |
| 4 | API1 — Broken Object Level Authorization | contrôle du propriétaire dans le service |
| 5 | API3 — Broken Object Property Level Authorization | le client ne choisit pas les champs sensibles |
