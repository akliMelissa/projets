#!/bin/bash

compare_lines() {
    local readelf_line="$1"
    local program_line="$2"

    # Supprimer les espaces multiples et les espaces en fin de ligne
    readelf_line_clean=$(echo "$readelf_line" | sed 's/ \+/ /g' | sed 's/[[:space:]]*$//')
    program_line_clean=$(echo "$program_line" | sed 's/ \+/ /g' | sed 's/[[:space:]]*$//')

    # Comparaison des lignes
    if [ "$readelf_line_clean" == "$program_line_clean" ]; then
        exit 0  # Les lignes correspondent
    else
        # Afficher les différences si elles existent
        echo -e "\e[31mDifférence trouvée :"
        echo -e "Ligne de readelf   : '$readelf_line_clean'"
        echo -e "Ligne du programme : '$program_line_clean'"
        echo -e "-------------------------------\e[0m"
        exit 1  # Les lignes ne correspondent pas
    fi
}

# Vérifier qu'un fichier a été passé en argument
if [ $# -eq 0 ]; then
  echo "Usage: $0 <fichier_ELF>"
  exit 1
fi

# Fichier ELF à tester
FILE=$1

# Vérifier si le fichier existe
if [ ! -f "$FILE" ];then
  echo "Le fichier $FILE n'existe pas."
  exit 1
fi

# Comparaison ligne par ligne en parallèle
found_difference=false

# Utilisation de paste pour comparer les lignes de la sortie de readelf et du programme
paste <(LC_ALL=C arm-none-eabi-readelf -S "$FILE") <(./Program S "$FILE") | while IFS=$'\t' read -r readelf_line program_line; do
    # Si la ligne commence par "Key to Flags:", arrêter la comparaison
    if [[ "$readelf_line" == "Key to Flags:"* ]]; then
        break
    fi

    if ! compare_lines "$readelf_line" "$program_line"; then
        found_difference=true
    fi
done

# Afficher le résultat final
if [ "$found_difference" = false ]; then
    echo -e "\e[32mLes tables de section sont identiques.\e[0m"
else
    echo -e "\e[31mLes tables de section sont différentes.\e[0m"
fi
