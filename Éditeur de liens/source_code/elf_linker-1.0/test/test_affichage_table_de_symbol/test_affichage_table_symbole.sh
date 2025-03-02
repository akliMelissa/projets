#!/bin/bash

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

# Récupérer le résultat de Program et de readelf
Program=$(./Program s "$FILE")
readelf=$(arm-none-eabi-readelf -s "$FILE")

# Formater chaque ligne pour avoir un seul espace entre les mots
formatted_program=$(echo "$Program" | sed 's/  */ /g')
formatted_readelf=$(echo "$readelf" | sed 's/  */ /g')

# Stocker les résultats dans des tableaux pour une comparaison ligne par ligne
IFS=$'\n' read -r -d '' -a program_lines <<< "$formatted_program"
IFS=$'\n' read -r -d '' -a readelf_lines <<< "$formatted_readelf"

# Obtenir le nombre maximal de lignes pour éviter les erreurs
max_lines=${#program_lines[@]}
if [ ${#readelf_lines[@]} -lt $max_lines ]; then
  max_lines=${#readelf_lines[@]}
fi

# Initialiser la variable qui indiquera s'il y a des différences
differences=0

# Comparer ligne par ligne
for ((i=0; i<max_lines; i++)); do
  if [ "${program_lines[i]}" != "${readelf_lines[i]}" ]; then
    echo "Différence à la ligne $((i+1)) :"
    echo "Program_: ${program_lines[i]}"
    echo "readelf_: ${readelf_lines[i]}"
    differences=1
    exit 1
  fi
done
#en vert

  echo -e "\e[32mLes deux sorties sont identiques.\e[0m"

