## Préparation du jeu de données

### Combien de lignes fait ce fichier ? (voir la commande wc).
Le fichier contient 13 849 287 lignes.

### Nous travaillerons d’abord sur un échantillon contenant les 100 mille premières lignes, générez celui-ci. (voir la commande head).

Sur Windows, on utilise :
```powershell
Get-Content agg_match_stats_0.csv -Head 100000 > agg_match_stats_0_100000.csv
```

###  Les meilleurs joueurs

