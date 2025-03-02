#!/bin/bash

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

# Obtenir le nombre de sections du fichier ELF
nb_sections=$(readelf -h "$FILE" | grep "Number of section headers:" | awk '{print $5}')

# Comparer les sections par numéro
for i in $(seq 0 $((nb_sections - 1))); do
    program=$(./Program x "$FILE" $i | tr -s ' ')
    readelf=$(arm-none-eabi-readelf -x $i "$FILE" | tr -s ' ')
    
    if [ "$program" != "$readelf" ]; then
        echo "Différence trouvée pour la section $i"
        echo "Program_: $program"
        echo "Readelf_: $readelf"
        exit 1
    fi
done

# Extraire et comparer les sections par nom
arm-none-eabi-readelf -WS "$FILE" | grep -o '\.[a-zA-Z0-9_.-]*' | while read -r section_name; do
    program=$(./Program x "$FILE" "$section_name" | tr -s ' ')
    readelf=$(arm-none-eabi-readelf -x "$section_name" "$FILE" | tr -s ' ')

    if [ "$program" != "$readelf" ]; then
        echo -e "\e[31mDifférence trouvée pour la section $section_name\e[0m"
        echo -e "Program_: $program"
        echo -e "Readelf_: $readelf"
        exit 1
    fi
done
#afficher en vert 
echo -e "\e[32mAucune différence trouvée.\e[0m"

exit 0