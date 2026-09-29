-- Deux clients, deux comptes chacun. Les id sont générés : 1 et 2 pour alice, 3 et 4 pour bob.
INSERT INTO compte (titulaire, proprietaire, type, solde) VALUES ('Alice Martin', 'alice', 'COURANT', 1500.00);
INSERT INTO compte (titulaire, proprietaire, type, solde) VALUES ('Alice Martin', 'alice', 'EPARGNE', 5000.00);
INSERT INTO compte (titulaire, proprietaire, type, solde) VALUES ('Bob Durand', 'bob', 'COURANT', 800.00);
INSERT INTO compte (titulaire, proprietaire, type, solde) VALUES ('Bob Durand', 'bob', 'EPARGNE', 2000.00);
