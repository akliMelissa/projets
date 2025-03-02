#!/bin/bash

# Chemin vers le répertoire contenant les fichiers .o
input_dir="./Examples_loader"

# Vérifier si un argument (adresse de base) a été fourni
if [[ $# -eq 1 ]]; then
    # Si un argument est fourni, l'utiliser comme adresse de base
    adresse_base="$1"
else
    # Sinon, utiliser une adresse de base par défaut
    adresse_base="0x1000"
fi

echo "Adresse de base utilisée : $adresse_base"

# Parcourir tous les fichiers .o dans le répertoire spécifié
for file in "$input_dir"/*.o; do
    # Vérifier si le fichier existe (au cas où il n'y a pas de fichiers .o)
    if [[ -f "$file" ]]; then
        # Extraire le nom du fichier sans le chemin
        filename=$(basename "$file")

        # Définir le nom du fichier de sortie (dans le répertoire courant ou un autre répertoire)
        output_file=./Examples_loader/"${filename%.o}_output.o"  # Exemple : fichier.o -> fichier_output.txt

        # Exécuter ./Executable avec les arguments requis
        ./Executable "$file" "$output_file" "$adresse_base" > /dev/null

        # Exécuter ./exc.sh avec le fichier de sortie généré
        ./test/test_exec/exc.sh "$output_file"
    else
        echo "Aucun fichier .o trouvé dans le répertoire $input_dir."
    fi
done