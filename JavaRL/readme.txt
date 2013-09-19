JavaRL - Bibliothèque d'apprentissage par renforcement en Java.

19/09/2013 Lance des experiences (batch) sur le HumanArm
  * Définit une expérience qui hérite de XPDefault (ex: XP0_TestConsigne ou DynSystem)
  * lance avec "./run.sh Main nomClassXP [paramXO ...]'
  * example ./run.sh simulator.Main simulator.XP0_TestConsigne -a0 10 -a1 25 -im 0 -cv 0.01 -ct 0.3 -n 10 -lf test_cons -mt 2

22/07/2013 Generic Simulator
  * Example de base avec le HumanArm
  * Utilisation de Parameters, lu depuis un fichier ou la ligne de commande.
  * Batch mode avec log Screen et/ou logFile

15/07/2013 LWR comme outil de Régression générique.
  * Uniquement testé en mono-dimension en sortie.

09/06/2013 LSPI avec 2 sous variantes (LSTDQ et LSTDQFAST)

L'idée est d'implémenter LSPI pour pouvoir le tester sur des problèmes comme le HumanArm.
LSPI, avec deux sous-variantes pour l'estimation de la fonction valeur de la politique,
est codé dans src/algorithme. Il est tiré de :

  Lagoudakis and Parr, 2003. Least square policy iteration. JMLR, no 4, pp1107-1149.

Pour le tester, le problème du 'ChainWalk' est codé dans src/problem.

Actuellement, il faut lancer src/test/TestLSPI pour tester LSPI. Des fichiers 'lspi_chain.out'
et 'lspi_chain_fast.out' sont créés, ils contiennent les valeurs des différents états
sur les itérations de LSPI. On peut afficher ces valeurs avec le fichier gnuplot 'plotQ.gpl'.

