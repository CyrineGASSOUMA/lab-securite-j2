#!/usr/bin/env bash
# Télécharge les dépendances Maven à la création du Codespace :
# le premier mvn spring-boot:run en séance est immédiat.
set -e
if [ -f lab-securite/banque-api/pom.xml ]; then
  mvn -q -B -f lab-securite/banque-api/pom.xml dependency:go-offline || true
  # Dépendance ajoutée par les participants à l'étape 1
  mvn -q -B dependency:get -Dartifact=org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.3.5 || true
fi
echo "Environnement prêt."
