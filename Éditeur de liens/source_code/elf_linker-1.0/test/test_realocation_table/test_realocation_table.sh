#!/bin/bash

# Vérifier qu'un fichier a été passé en argument
if [ $# -eq 0 ]; then
  echo "Usage: $0 <fichier_ELF>"
  exit 1
fi

# Fichier ELF à tester
FILE=$1
chemin="./test"

# Vérifier si le fichier existe
if [ ! -f "$FILE" ];then
  echo "Le fichier $FILE n'existe pas."
  exit 1
fi

    program=$(./Program r "$FILE")
    readelf=$(arm-none-eabi-readelf -r "$FILE")
    #formatter a un seul espace
    program=$(echo $program | tr -s " ")
    readelf=$(echo $readelf | tr -s " ")
    if [ "$program" != "$readelf" ]; then
        echo "Erreur: $i"
        echo "Program: $program"
        echo 
        echo "Readelf: $readelf"
        exit 1
    fi
    #en vert 
    echo -e "\e[32mTest réussi pour le fichier: $FILE\e[0m"
    exit 0


