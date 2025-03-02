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
#si le fichier est vide
if [ ! -s "$1" ]; then
    exit 0
fi
echo "$1"
current_section=""
section_count=0
# Lire le fichier ligne par ligne
while IFS= read -r line; do
    # Si on trouve une ligne qui commence par "Contenu de section avant:",
    # c'est le début d'une nouvelle section
    if [[ "$line" == "Contenu de section avant :"* ]]; then
        # Si nous avons déjà une section en cours, on la sauvegarde
        if [ ! -z "$current_section" ]; then
            echo "$current_section" > "./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/section_$section_count.txt"
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
echo "Nombre total de sections trouvées: $section_count"


# Sauvegarder la dernière section
if [ ! -z "$current_section" ]; then
    echo "$current_section" > "./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/section_$section_count.txt"
    section_count=$((section_count + 1))
fi

# Appeler le script d'analyse pour chaque section   
for ((i=0; i<section_count; i++)); do
    echo "=== Traitement de la section $i ==="
    echo "----------------------------------------"
    # Remplacer 'analyse_section.sh' par le nom de votre script précédent
    # si ca retourne 0, c'est que le test a réussi
    if ./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/test_chaque_sections2.sh "./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/section_$i.txt"; then
        echo "Test réussi pour la section $i"
        
    else
        echo "Test échoué pour la section $i"
        #rm  -f ./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/*.txt
        exit 1
    fi
done