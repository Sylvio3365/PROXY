echo "Compilation des fichiers Java..."
javac -d . *.java
# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Démarrage du proxy..."
    java proxy.Start
else
    echo "Erreur de compilation. Assurez-vous que tous les fichiers .java sont présents."
fi
