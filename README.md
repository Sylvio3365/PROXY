# Proxy Cache Project


## Description

Ce projet implémente un serveur proxy avec un système de mise en cache pour intercepter les requêtes HTTP des clients. Le serveur vérifie si la réponse à une requête est déjà présente dans le cache, et si ce n'est pas le cas, il envoie la requête à un serveur Apache distant pour obtenir la réponse. Le serveur met ensuite en cache cette réponse pour les requêtes futures. Le cache est nettoyé automatiquement à intervalles réguliers selon un temps d'expiration défini dans un fichier de configuration.

## Fonctionnalités

- **Mise en cache des réponses HTTP** : Le serveur proxy met en cache les réponses HTTP pour réduire les requêtes répétées au serveur distant. Lorsqu'une réponse est demandée, le serveur proxy vérifie si elle est déjà présente dans le cache et la renvoie, sinon il envoie la requête au serveur Apache distant et stocke la réponse dans le cache.
  
- **Journalisation des requêtes** : Le serveur proxy enregistre les actions importantes (comme les requêtes HTTP, les erreurs et les réponses) dans un fichier de log. Vous pouvez consulter les logs via la commande `show log` dans l'interface en ligne de commande.

- **Lecture de la configuration** : Le serveur proxy lit un fichier de configuration (`config.conf`) pour obtenir les paramètres nécessaires, comme l'adresse IP du serveur Apache, le port, et le délai d'expiration des entrées du cache.

- **Nettoyage automatique du cache** : Le cache est nettoyé régulièrement en fonction du temps d'expiration défini dans le fichier de configuration. Ce nettoyage permet d'éviter l'accumulation de données obsolètes et de libérer de l'espace pour de nouvelles entrées.

- **Interface en ligne de commande** : Le serveur proxy propose une interface en ligne de commande pour gérer les différentes fonctionnalités et interagir avec le programme. Voici les commandes disponibles et leur comportement :

  - **`stop`, `bye`, `exit`** : Ces commandes arrêtent le serveur proxy et quittent le programme. Elles permettent de fermer proprement le serveur et de terminer l'exécution du programme.
  
  - **`edit conf`** : Cette commande permet d'ouvrir le fichier de configuration du proxy dans un éditeur de texte (`nano` par défaut). Vous pouvez ainsi modifier les paramètres du proxy, tels que l'adresse IP du serveur Apache ou le port du proxy.
  
  - **`myconfig`** : Affiche les détails de la configuration actuelle du proxy, y compris les paramètres comme l'adresse IP et le port du serveur Apache, ainsi que le délai d'expiration du cache.
  
  - **`ls`** : Affiche la liste des fichiers actuellement stockés dans le cache du proxy. Cette commande permet de vérifier quelles réponses sont mises en cache et d'inspecter les données stockées.
  
  - **`rm`, `del`, `clear`** : Ces commandes permettent de supprimer les fichiers du cache. Si l'argument `all` est fourni, elles videront complètement le cache. Sinon, vous pouvez spécifier un index pour supprimer un fichier particulier dans le cache. Exemple :
    - `rm all` ou `clear` : Vide entièrement le cache.
    - `rm <index>` : Supprime l'élément spécifique à l'index donné du cache.

  
  - **`allow ip <IP_ADDRESS>`** : Cette commande permet d'autoriser une adresse IP spécifique à accéder au proxy. Elle est utile pour gérer les connexions entrantes et autoriser l'accès au proxy depuis des adresses IP spécifiques.
  
  - **`deny ip <IP_ADDRESS>`** : Cette commande bloque une adresse IP spécifique, empêchant cette adresse de se connecter au proxy. Elle peut être utilisée pour interdire l'accès à certaines adresses IP.
  
  - **`show denied ip`** : Affiche la liste des adresses IP actuellement bloquées par le proxy. Cela permet de voir quelles adresses IP sont interdites et de les gérer si nécessaire.

  - **`show log`** : 
## Prérequis

- **Java 8 ou version supérieure**.
- Une machine avec une connexion réseau pour interagir avec un serveur Apache.
- Un fichier de configuration `config.conf` pour définir les paramètres du serveur proxy.

## Lancement du programme

  - **`./run.sh`** :

