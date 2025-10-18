---
title: SAE S2.02 -- Rapport graphes
subtitle: Équipe Les Gars-fiques
author: Ethan Seulin, Alexandre Lepoutre, Alban Sonneville, 
date: 2025
---



# Version 1

## Choix pour la modélisation

#### Règles sur les affinités:

**- Passe-temps (préférence).** Plus les adolescents ont de passe-temps en commun, plus leur affinité est importante.

\- **Genre (préférence).** On considère qu’il y a affinité pour chaque préférence de genre satisfaite (c’est-à-dire, il y a un bonus d’affinité important si les préférences de genre des deux adolescents sont satisfaites. Ce bonus d’affinité est moindre si une seule des préférences de genre est satisfaite). Notons qu’une préférence non exprimée (une valeur vide pour PAIR_GENDER) est considérée comme étant toujours satisfaite.

\- **Différence d’âge (préférence).** Une différence d’âge inférieure à 1 an et demi apporte un bonus d’affinité.

### Forte affinité

| NAME | GENDER | PAIR_GENDER | BIRTH_DATE | HOBBIES             |
|------|--------|:------------|------------|---------------------|
| H1   | MAN    | WOMAN       | 15/01/2006 | video games, baking |
| V1   | WOMAN  | MAN         | 15/04/2006 | video games, foot   |

Il y a ici une forte affinité car V1 et H1 ont le même genre, la même préférence de genre pour l'hôte ou visiteur, et les mêmes hobbies.

### Faible affinité

| NAME | GENDER | PAIR_GENDER | BIRTH_DATE | HOBBIES        |
|------|--------|:------------|------------|----------------|
| H2   | WOMAN  | WOMAN       | 23/01/2005 | bowling, games |
| V2   | MAN    | MAN         | 11/12/2006 | music, baking  |

Ici il y a faible affinité, on peut aussi dire nulle car aucune règle les définissants comme compatible. Les préférences de genre sont opposés, il y a plus d'un an et demi de différence d'âge et ils n'ont aucun hobbies en commun.

### Arbitrage entre les critères d'affinité

| NAME | GENDER | PAIR_GENDER | BIRTH_DATE | HOBBIES           |
|------|--------|:------------|------------|-------------------|
| H3   | WOMAN  | WOMAN       | 15/01/2006 | video games; foot |
| V3   | WOMAN  | MAN         | 15/02/2004 | foot; video games |
| H4   | MAN    | WOMAN       | 15/03/2006 | tennis; ski       |
| V4   | WOMAN  | MAN         | 15/04/2004 | video games; foot |
| H5   | MAN    | WOMAN       | 15/05/2006 | bowling; baking   |
| V5   | WOMAN  | WOMAN       | 15/06/2006 | sewing; ski       |

Ici les affinités moyennes sont définies par au moins une caractéristique en commun.

Dans le **premier** cas les hobbies,  
dans le **deuxième** les préférences de genre,  
dans le **troisième** ils sont de la même année.



## Exemple complet

| NAME | GENDER | PAIR_GENDER | BIRTH_DATE | HOBBIES         |
|------|--------|:------------|------------|-----------------|
| A    | WOMAN  | MAN         | 15/01/2004 | games, foot     |
| B    | WOMAN  | MAN         | 15/02/2008 | bowling, games  |
| C    | MAN    | WOMAN       | 15/03/2004 | tennis, ski     |
| D    | WOMAN  | WOMAN       | 12/06/2006 | basket, games   |
| W    | WOMAN  | MAN         | 15/04/2008 | tennis, ski     |
| X    | MAN    | WOMAN       | 15/05/2008 | bowling, baking |
| Y    | WOMAN  | WOMAN       | 15/06/2006 | sewing, ski     |
| Z    | MAN    | WOMAN       | 16/11/2004 | foot, games     |

##### Affinités les plus optimales pour les hôtes/voyageurs selon les critères définis

| HOTES | VISITEURS | POINTS (compatibilité) | hobbies en commun | Diff de mois |
|-------|-----------|:-----------------------|-------------------|--------------|
| A     | Z         | **5**                      | foot, games       | 10           |
| B     | X         | **4**                      | bowling           | 3            |
| C     | W         | **4**                      | tennis, ski       | 49           |
| D     | Y         | **3**                      | (aucun)           | 0            |

```
double score_affinité_1(hôte, visiteur) :

score = 0 (la valeur initiale du score)

si la différence d'age entre hôte est visiteur est inférieure à 18 mois,
alors on ajoute 1 au score

si le genre demandé par le visiteur correspond au genre du hôte,
alors on ajoute 1 au score

si le genre demandé par le hôte correspond au genre du visiteur,
alors on ajoute 1 au score

si hote et visiteur ont N hobbies en commun,
alors on ajoute N au score

retourner le score
```

## Retour sur l'exemple

`score_affinité_1`*.*

__Matrice d'adjacence :__

```
    A  B  C  D 
  --------------
W | 0  1  4  1 |
X | 2  4  0  1 |
Y | 1  1  2  3 |
Z | 5  3  1  3 |
  --------------
```

On a donc comme meilleur appariement les affinités : \[A,Z\] \[B,X\] \[C,W\] \[D,Y\].



# Version 2

*Ci-dessous, vous définirez des hôtes ayant des noms A1, A2, B1, B2, etc., et des visiteurs ayant des noms W1, W2, X1, X2, etc. Pour chacun et chacune d'entre iels, vous devrez donner :*

- *la valeur pour la colonne NAME parmi A1, A2, B1, ..., W1, W2, X1, ... ;*
- *des valeurs pour les colonnes HOBBIES, GENDER, PAIR_GENDER, BIRTH_DATE pour tout le monde ;*
- *des valeurs pour les colonnes HOST_HAS_ANIMAL, HOST_FOOD pour les hôtes ;*
- *des valeurs pour les colonnes GUEST_ANIMAL_ALLERGY, GUEST_FOOD_CONSTRAINT pour les visiteurs.*

| NAME | GENDER | PAIR_G | BIRTH_DAY  | HOBBIES            |     | FOOD          | ALLERGY | FOOD_CONSTRAINT |
|------|--------|--------|------------|--------------------|-----|---------------|---------|-----------------|
| A1   | MAN    | WOMAN  | 2004-04-10 | Reading, Hiking    | Yes | Vegetarian    |         |                 |
| A2   | WOMAN  | MAN    | 2005-06-14 | Painting, Swimming | Yes | Vegan         |         |                 |
| B1   | WOMAN  | WOMAN  | 2004-11-02 | Cycling, Gaming    | No  | No preference |         |                 |
| B2   | WOMAN  | MAN    | 2006-09-27 | Cooking, Football  | Yes | Gluten-Free   |         |                 |
| C1   | WOMAN  | MAN    | 2005-12-22 | Traveling, Reading | No  | Vegetarian    |         |                 |
| C2   | MAN    | WOMAN  | 2003-08-18 | Skiing, Painting   | No  | Vegan         |         |                 |
| D1   | WOMAN  | MAN    | 2006-07-11 | Gaming, Hiking     | No  | No preference |         |                 |
| D2   | MAN    | WOMAN  | 2001-03-03 | Swimming, Cycling  | Yes | Gluten-Free   |         |                 |
| W1   | MAN    | WOMAN  | 2002-01-05 | Football, Cooking  |     |               | No      | Vegetarian      |
| W2   | WOMAN  | MAN    | 2000-10-29 | Painting, Gaming   |     |               | Yes     | Vegan           |
| X1   | MAN    | WOMAN  | 2005-02-15 | Skiing, Traveling  |     |               | No      | No preference   |
| X2   | WOMAN  | MAN    | 2004-04-24 | Reading, Swimming  |     |               | No      | Gluten-Free     |
| Y1   | WOMAN  | MAN    | 2004-09-16 | Hiking, Cycling    |     |               | Yes     | Vegetarian      |
| Y2   | MAN    | WOMAN  | 2002-06-30 | Gaming, Football   |     |               | No      | Vegan           |
| Z1   | MAN    | MAN    | 2005-08-13 | Cooking, Painting  |     |               | No      | No preference   |
| Z2   | WOMAN  | MAN    | 2006-12-07 | Football, Hiking   |     |               | Yes     | Gluten-Free     |



## Exemple avec appariement total

*Donnez un exemple de quatre hôtes A1, B1, C1, D1 et quatre visiteurs W1, X1, Y1, Z1 pour lesquels il existe des incompatibilités entre certains hôtes et certains visiteurs, mais il est possible de trouver un appariement qui respecte les contraintes rédhibitoires.*

Nous pouvons par exemple associé les hôtes et les visiteurs comme ceci : 

A1 avec Z1; B1 avec Y1; C1 avec W1; D1 avec X1;

Où chaque hôte peux prendre en compte les contraintes rédhibitoires des visiteurs, même s'il existe des incompatibilité entre eux.

*Donnez également l'appariement que vous considérez le meilleur pour cet exemple. Expliquez pourquoi.*

Le meilleur appariement pour cet exemple est le suivant :  
A1 et W1, ils n'ont pas de préférence de genre en commun, ni de hobbies, par contre ils sont compatibles pour les animaux ainsi que pour l'alimentation.  


## Exemple sans appariement total

*Donnez un exemple de quatre hôtes A2, B2, C2, D2 et quatre visiteurs W2, X2, Y2, Z2 pour lesquels il n'est pas possible de former quatre paires hôte-visiteur à cause d'incompatibilités.*  
A cause d'incompatibilités, il serait impossible de former les groupes de la manière suivante :

A2 avec W2 / B2 avec Z2  / C2 avec X2 / D2 avec Y2

*Pour cet exemple, quel est le plus grand nombre de paires qu'on peut former ?*

Pour cet exemple, nous pouvons former un maximum de 11 paires.

(A2 W2; A2 X2; A2 Z2; B2 W2; B2 Y2; B2 Z2; C2 X2; C2 Z2; D2 W2; D2 Y2;  D2 Z2)

*Donnez l'appariement que vous considérez le meilleur. Expliquez pourquoi.*

Le meilleur appariement pour est le suivant :

C2 et W2, car ils ont les affinités de genre en commun ainsi qu'un hobbie, c'est donc les personnes avec le plus de traits en commun parmi celle là.

## Score d'affinité

*Donner le pseudo-code de la fonction* `score_affinité_2(hôte, visiteur)` *qui retourne un nombre représentant le degré d'affinité entre un hôte et un visiteur. Vous pouvez réutiliser la fonction* `score_affinité_1` *(l'appeler ou copier du code).*

```
double score_affinité_2(hôte, visiteur) 
  // compléter le code ici

  score = 0 (la valeur initiale du score)
  
  si l'hôte a un animal et que le visiteur y est alergique,
	alors score = null

  si l'hote a un regime alimentaire,
	si le visiteur a un regime alimentaire different de l'hote
	qui n'est pas nul,
		alors score = null
  sinon,
	si la différence d'age entre hôte est visiteur est 
	inférieure à 18 mois,
	alors on ajoute 30 au score

	si le genre demandé par le visiteur correspond au 
	genre du hôte,
		alors on ajoute 20 au score

	si le genre demandé par le hôte correspond au 
	genre du visiteur,
		alors on ajoute 20 au score

	si hote et visiteur ont N hobbies en commun,
		alors on ajoute 10 fois N au score

  retourner score
```



## Retour sur l'exemple

*Donnez les matrices d'adjacence pour les deux exemples de la Version 2 (A1,B1,C1,D1/W1,X1,Y1,Z1 et A2,B2,C2,D2/W2,X2,Y2,Z2). Les poids des arêtes sont déterminés par la fonction* `score_affinité_2`*. Pensez à nommer les lignes et les colonnes.*

*Calculez l'appariement de poids minimal pour chacun des graphes. Obtenez-vous l'appariement que vous aviez identifié comme le meilleur ?*

__Matrice d'adjacence :__

```
     A1   B1   C1  D1 
  -------------------
W1 |  0    0   40  40 |
X1 | 30   50   80  70 |
Y1 | -1   60   30  10 |
Z1 | 50   30   50  50 |
  -------------------
On retrouve bel et bien A1 et W1 à 0 d'apparieùent, 
car ils n'ont pas de contraintes rhédibitoires, mais ils n'ont aucun critères en commun.
(En cas de contrainte rhédibitoire, valeur à -1)

     A2  B2  C2  D2 
  -----------------
W2 | -1  -1  50  -1 |
X2 | -1  20  -1  50 |
Y2 | 40  -1  30  -1 |
Z2 | -1  -1  -1  -1 |
  -----------------
On retrouve bel et bien C2 et W2 avec le plus de score d'appariement car ils ont beaucoup de critères en commun. (Tout en respectant les contraintes rhédibitoires)

Ici, les meilleurs appariement sont :
A2 avec Y2; B2 avec Personne; C2 avec W2 et D2 avec X2.
Z2 n'étant compatible avec personne, il ne pourra pas être accueilli.
```

## Robustesse de la modélisation (question difficile)

*Est-ce que votre fonction* `score_affinité_2` *garantit que les contraintes rédhibitoires seront toujours respectées, quel que soit le jeu de données ? Justifiez votre réponse.*

***Indications**** : Cherchez un exemple de **grande taille** pour lequel la fonction* `score_affinité_2` *pourrait ne pas garantir le respect des contraintes. Dans cet exemple, vous auriez beaucoup d'adolescents compatibles sans affinité, et quelques adolescents incompatibles avec beaucoup d'affinité.*

*Il est possible que votre fonction garantisse le respect des contraintes quel que soit l'exemple. Si vous pensez que c'est le cas, donnez des arguments pour convaincre.*

La fonction score_affinité\_2() garantit que les contraintes rédhibitoires seront respectées, car si elles ne le sont pas, le score donné est attribué à -1. Sinon, le score sera positif.

Un score de valeur -1 rend automatiquement incompatible la paire d'étudiants.



# Version 3

*Ci-dessous, H1, H2, etc. désignent des noms d'hôtes et V1, V2, etc désignent des noms de visiteurs. Pour chacun et chacune d'entre iels, vous devrez donner des valeurs pour toutes les colonnes pertinentes en fonction de leur rôle, hôte ou visiteur.*

| NAME | GENDER | BIRTH_DATE | HOBBIES              | G | ANIMAL | FOOD        | Anilal_ALLERGY | CONSTRAINT |
|------|--------|------------|----------------------|---|--------|-------------|----------------|------------|
| H1   | F      | 2007-05-12 | Hiking, reading      | M | yes    | Vegetarian  |                |            |
| V1   | M      | 2006-11-03 | Hiking, reading      | F |        |             | no             | Vegetarian |
| H2   | M      | 2008-03-21 | Painting, video game | F | yes    | Gluten-Free |                |            |
| V2   | F      | 2008-04-10 | Painting, video game | M |        |             | yes            | Vegetarian |
| H3   | M      | 2007-09-08 | Sport, voyages       | F | no     | Vegetarian  |                |            |
| V3   | F      | 2007-12-14 | Sport, Traveling     | M |        |             | no             | No         |
| H4   | F      | 2003-06-30 | Sport, Reading       | M | yes    | Gluten-Free |                |            |
| V4   | M      | 2006-07-01 | Horse, Swimming      | F |        |             | yes            | Vegetarian |



## Équilibrage entre affinité / incompatibilité

*Donnez au moins quatre paires hôte-visiteur (H1, V1), (H2, V2), (H3, V3), (H4, V4), ... que vous considérez quasi équivalents pour l'affectation. Certaines de ces paires doivent ne pas respecter les contraintes considérées rédhibitoires dans la Version 2, d'autres doivent les respecter. Ces exemples doivent illustrer l'équilibrage que vous faites entre l'incompatibilité d'une part et l'affinité d'autre part : combien et quel type d'affinité permet de compenser combien et quel type d'incompatibilité. Les exemples seront accompagnés de commentaires expliquant vos choix.*

| PAIRE    | HÔTE                     | VISITEUR                                         | CONTRAINTE(S) RÉDHIBITOIRE(S)                    | AFFINITÉ(S) PRINCIPALE(S)                                                     |
|----------|--------------------------|--------------------------------------------------|--------------------------------------------------|-------------------------------------------------------------------------------|
| (H1, V1) | Animal, végétarien       | Végétarien, pas allergique                       | Aucune                                           | Même régime alimentaire, aime la randonnée et la lecture                      |
| (H2, V2) | A un animal, sans gluten | Allergique aux animaux, végétalien               | Allergie aux animaux                             | Forts intérêts communs : peinture, jeux vidéos, profils de même tranche d'âge |
| (H3, V3) | Pas d’animal, végétarien | Pas allergique, pas d'alimentation particulière) | Aucune                                           | Passion pour le sport et les voyages.                                         |
| (H4, V4) | Animal, sans gluten      | Allergique aux animaux, végétarien               | Divergence alimentaire, et allergie aux animaux. | Aucune entente sur les hobbies et l'âge.                                      |

Pour le 1er exemple (H1 et V1) : La compatibilité est parfaite en tout point. Le score sera maximal.

Pour le second exemple (H2 et V2) : Contraintes rédhibitoires , mais tout le reste est bon.

Malgré une forte affinité, la création de ce groupe reste impossible à cause des contraintes rédhibitoires. Le score sera alors de -1.

Pour le troisième exemple (H3 et V3) : Pas de contrainte rédhibitoires, mais hobbies en commun.

Le score sera moyen. (Ni nul, ni trop élevé)

Pour le quatrième exemple (H4 et V4) : La compatibilité est nulle, il n'y a aucun élément en commun.

Le score sera donc de -1.



## Score d'affinité

*Donner le pseudo-code de la fonction* `score_affinité_3(hôte, visiteur)` *qui retourne un nombre représentant le degré d'affinité entre un hôte et un visiteur. Vous pouvez réutiliser les fonctions* `score_affinité_1` *et* `score_affinité_2`*.*

```
double score_affinité_3(hôte, visiteur) 
  // compléter le code ici
  
  score = score_affinité_2(hôte, visiteur)

  si score est différent de -1, // Si le groupe est compatible
	alors si le régime alimentaire de l'hote = celui du visiteur,
		alors on ajoute 20 au score
	si l'hote a un animal et que le visiteur n'y est pas allergique,
		alors on joute 10 au score
	si l'hote n'a pas d'animal et que le visiteur est alergique aux animaux,
		alors on ajoute 10 au score
  retourner score
```

## Retour sur l'exemple

*Donnez le résultat de la fonction* `score_affinité_3` *pour les exemples d'équilibrage (H1, V1), (H2, V2), etc. ci-dessus. Est-ce que vous obtenez des scores proches ?*

***Remarque****: Deux scores ne sont pas proches ou éloignés dans l'absolu ; cela dépend de la valeur minimale et la valeur maximale que peut prendre le score. Par exemple, les nombres 10 et 20 sont "proches" à l'échelle de l'intervalle de 0 à 1000, mais ne sont pas "proches" à l'échelle de l'intervalle 0 à 30.*

| Paire  | Score                          |
|--------|--------------------------------|
| H1, V1 | 120 (Score maximal obtensible) |
| H2, V2 | \-1 (Score minimal obtensible)  |
| H3, V3 | 80 (Score très correct)        |
| H4, V4 | \-1                             |

Plus le score est élevé, plus l'affinité entre deux individu est forte, à l'inverse, plus elle est faible.

Pour le score, on a une échelle de -1 (Incompatible) jusque 120 (Parfaitement compatible).