#!/bin/bash

# Vérification du paramètre
if [ $# -ne 1 ]; then
    echo "Usage: $0 <fichier_entrée>"
    exit 1
fi

# Vérification de l'existence du fichier
if [ ! -f "$1" ]; then
    echo "Erreur: Le fichier $1 n'existe pas"
    exit 1
fi

# Variable pour stocker temporairement une section
current_section=""
section_count=0

# Répertoire de sortie
output_dir="./test/Reimplantations_de_type_R_ARM_ABS"



# Lire le fichier ligne par ligne
while IFS= read -r line; do
    # Si on trouve une ligne qui commence par "Contenu de section avant:",
    # c'est le début d'une nouvelle section
    if [[ "$line" == "Contenu de section avant:"* ]]; then
        # Si nous avons déjà une section en cours, on la sauvegarde
        if [ ! -z "$current_section" ]; then
            echo "$current_section" > "$output_dir/section_$section_count.txt"
            section_count=$((section_count + 1))
        fi
        # Commencer une nouvelle section
        current_section="$line"
    else
        # Ajouter la ligne à la section en cours
        if [ ! -z "$current_section" ]; then
            current_section+=$'\n'"$line"
        fi
    fi
done < "$1"

# Sauvegarder la dernière section
if [ ! -z "$current_section" ]; then
    echo "$current_section" > "$output_dir/section_$section_count.txt"
    section_count=$((section_count + 1))
fi

echo "Nombre total de sections trouvées: $section_count"

# Appeler le script d'analyse pour chaque section
for ((i=0; i<section_count; i++)); do
    echo "=== Traitement de la section $i ==="
    echo "----------------------------------------"
    
    # Vérifier si le fichier d'analyse existe
    if [ ! -f "$output_dir/test_chaque_sections1.sh" ]; then
        echo "Erreur: Le script d'analyse $output_dir/test_chaque_sections1.sh n'existe pas"
        exit 1
    fi

    # Exécuter le script d'analyse
    if "$output_dir/test_chaque_sections1.sh" "$output_dir/section_$i.txt"; then
        echo "Test réussi pour la section $i"
    else
        echo "Test échoué pour la section $i"
        # Nettoyage des fichiers temporaires dans le répertoire
        exit 1
    fi 

    echo "----------------------------------------"
done

# Nettoyage (optionnel - décommenter si vous voulez supprimer les fichiers temporaires)

exit 0
