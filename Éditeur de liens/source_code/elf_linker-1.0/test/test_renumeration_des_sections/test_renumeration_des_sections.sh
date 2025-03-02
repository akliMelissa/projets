#!/bin/bash

# Vérifier qu'un fichier a été passé en argument
if [ $# -eq 0 ]; then
  echo "Usage: $0 <fichier_ELF>"
  exit 1
fi

# Fichier ELF à tester
FILE=$1
FILE_OUT=$2
chemin="./test"

# Vérifier si le fichier existe
if [ ! -f "$FILE" ];then
  echo "Le fichier $FILE n'existe pas."
  exit 1
fi
avant=$(./Program h $FILE | grep "Number of section header:" | awk '{print $NF}')
nbsections=$(./Program S $FILE | grep -c "REL")
#./Program h $FILE
#sortie standard
./Program2 $FILE $FILE_OUT > /dev/null 
apres=$(./Program h $FILE_OUT | grep "Number of section header:" | awk '{print $NF}')
./test_elf_coherence $FILE > /dev/null
if [ $? -eq 0 ]; then
    #en vert
    echo -e "\e[32mTest Headers Passed.\e[0m"
else
    #en rouge
    echo -e  "\e[31mUne ou plusieurs assertions ont échoué.\e[0m"
    exit 1
fi
#si c'est juste la somme entre apres et nbsections est egale a avant
if [ $((apres+nbsections)) -eq $avant ]; then
  #en vert 
  echo -e "\e[32mTest section  passed.\e[0m"
  exit 0
else
  exit 1
fi