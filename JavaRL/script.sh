# --- Test génération, visualisation sur la consigne 0
# Nettoie un peu
rm test_cons*
# Genere les test et les PNG récapitulatif pour chaque combinaison
# de parametres
Rscript plotTestConsigne.R generate
# Produit des fichier PNG supplémentaires, pour le muscle 2 et
# pour les paramètres choisis
Rscript plotTestConsigne.R visu 2 test_cons-a0_*-a1_*-im2*.data
# Visualisation
eog *.png

# ---- Script de bas niveau pour lancer Java
# Lance le test en (10,25), sur la Consigne 0, 5 fois.
# ./run.sh simulator.Main simulator.XP0_TestConsigne -a0 10 -a1 25 -im 0 -cv 0.1 -ct 0.3 -lf test_cons -n 5 -mt 1
