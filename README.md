# Projet-INFO-4B

# Présentation :

#### Un serveur, gère les connexions d'un ou plusieurs clients, les clients effectuent des requêtes en rapport avec les echecs, le serveur cherchera dans un fichier texte au format pgn les données voulu du client et lui enverra les informations.

#### Le projet est réalisé en java, les fichiers utilisés comme base de données ont été prises sur le site https://database.lichess.org/.

## Pré-requis :

- Minimum 4 coeurs 8 threads.
- Pour un traitement de 0-10 go de données 16 go de ram, 10-100 go de données 32 go de ram, plus de 100 go 64 go de ram.
- Un dossier "data" contenant les fichiers de données situé dans le repertoire courant de l'éxcutable "Serveur.jar", ou
  le repertoire courant du projet.

## Fonctionnement :

Pour lancer le serveur :

`
java -Xmx32g -jar Serveur.jar
`
-Xmx32g signifie que vous allouez 32 go à la jvm, à la place de 32, mettez la quantité de ram que vous avez en go.

Pour lancer le client :

`
java -jar Client.jar
`

## Le client a 10 choix au total :

- 0 Pour quitter le mode itératif si et seulement s'il est sur des données iterative.
- 1 Consulter une partie spécifique et la visualiser pas à pas.
- 2 Trouver toutes les parties d’un joueur.
- 3 Consulter les 5 ouvertures les plus jouées.
- 4 Consulter les parties terminées avec n coups.
- 5 Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc.
- 6 Calculer le joueur le plus fort au sens du PageRank.
- 7 Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties.
- 8 Afficher toutes les parties.
- 9 Afficher le nombre de parties.
- -1 Pour quitter le serveur.
