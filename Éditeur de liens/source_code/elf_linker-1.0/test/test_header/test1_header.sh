#!/bin/bash

# Fonction pour comparer deux lignes, en ignorant la casse
compare_lines() {
  local readelf_line="$1"
  local program_line="$2"

  # Extraire la partie commune (avant la première valeur numérique ou symbolique)
  common_prefix_readelf=$(echo "$readelf_line" | cut -d ':' -f 1 | tr -d '[:space:]' | tr '[:upper:]' '[:lower:]')
  common_prefix_program=$(echo "$program_line" | cut -d ':' -f 1 | tr -d '[:space:]' | tr '[:upper:]' '[:lower:]')

  # Comparer les parties avant les ":"
  if [[ "$common_prefix_readelf" == "$common_prefix_program" ]]; then
    # Les préfixes correspondent, maintenant on compare les valeurs après ":"
    
    # Extraire les valeurs après le préfixe et les mettre en minuscules
    readelf_value=$(echo "$readelf_line" | cut -d ':' -f 2- | tr -d '[:space:]' | tr '[:upper:]' '[:lower:]')
    program_value=$(echo "$program_line" | cut -d ':' -f 2- | tr -d '[:space:]' | tr '[:upper:]' '[:lower:]')

    # Comparer les valeurs après le préfixe
    if [[ "$readelf_value" == "$program_value" ]]; then
      return 0  # Les lignes sont identiques après la partie commune
    else
      echo "Différence dans les valeurs :"
      echo "readelf: $readelf_value"
      echo "programme: $program_value"
      return 1  # Les valeurs après le préfixe sont différentes
    fi
  else
    return 1  # Les préfixes sont différents
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
if [ ! -f "$FILE" ]; then
  echo "Le fichier $FILE n'existe pas."
  exit 1
fi
i=0
found_match=false
# Exécuter ton programme et lire sa sortie ligne par ligne
./Program h "$FILE" | while read -r program_line; do
#si la ligne comment par Key to Flags: 
  
    LC_ALL=C arm-none-eabi-readelf -h "$FILE" | while read -r readelf_line; do
        # Comparer les lignes
        if compare_lines "$readelf_line" "$program_line"; then
            found_match=true
            break
        fi

    done
     
  
done

if [ "$found_match" = false ]; then
    echo -e "\e[32mLes entetes sont identiques pour le fichier $(basename $FILE)\e[0m"
else
    echo -e "\e[31mLes entêtes sont différentes pour le fichier $(basename $FILE)\e[0m"

fi
