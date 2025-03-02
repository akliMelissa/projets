#!/bin/bash

# Répertoire contenant les fichiers ELF
chemin="./Examples_loader"
chemin_script="./test"

# Vérifier si le répertoire existe
if [ ! -d "$chemin" ]; then
  echo "Le répertoire n'existe pas."
  exit 1
fi

# Si aucun argument n'est donné, exécuter tous les tests
if [ -z "$1" ]; then
  echo "Aucun argument spécifié, exécution de tous les tests..."
  sequences="1 2 3 4 5 6 7 8 9"
else
  sequences="$1"
fi

if ! ls $chemin/example*.o 1> /dev/null 2>&1
then
    echo "Des fichiers .o pour les tests n'existent pas dans le répertoire."
    echo "Tapez make  pour les generer"
    exit 1
fi

# Exécuter les boucles en fonction des séquences définies
for seq in $sequences; do
  case "$seq" in

    1)
      
      # Boucle 1 - Test header
      echo "--Exécution de la boucle 1 - Test header--"
      for s_file in $chemin/example*.o; do
        if [ -f "$s_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $s_file)"
          $chemin_script/test_header/test1_header.sh "$s_file"
        else
          echo "    Le fichier $s_file n'est pas un fichier ELF valide."
        fi
      done
      ;;

    2)
      # Boucle 2 - Test de la table d'index de section
      echo "--Exécution de la boucle 2 - Test de la table d'index de section--"
      for elf_file in $chemin/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          $chemin_script/test_index_table/test_index_table.sh "$elf_file"
        fi
      done
      ;;

    3)
      # Boucle 3 - Test de la table de sections
      echo "--Exécution de la boucle 3 - Test de la table de sections--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          $chemin_script/test_print_section_table/test_print_section.sh "$elf_file"

        fi
      done
      ;;
    4)
      #boucle 4 - Test de la table de symbole
      echo "--Exécution de la boucle 4 - Affichage de la table de symbole--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          $chemin_script/test_affichage_table_de_symbol/test_affichage_table_symbole.sh "$elf_file"
        fi
      done
    ;;

    5)
      # Boucle 5 - Test de la table de réallocation
      echo "--Exécution de la boucle 5 - Test de la table de réallocation--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          $chemin_script/test_realocation_table/test_realocation_table.sh "$elf_file"
        fi
      done
      ;;

    6)
      # Boucle 6 - Test de la renumérotation des sections
      echo "--Exécution de la boucle 6 - Test de la renumérotation des sections--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          if "$chemin_script/test_renumeration_des_sections/test_renumeration_des_sections.sh" "$elf_file" output.o; then
            echo "        Test passed for $elf_file"
          else
            echo "        Test failed for $elf_file"
            break
          fi
        fi
      done
      ;;
      7)
      # Boucle 7 - Test de la correction des symboles
      echo " -- Test de la correction des symboles--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          if "$chemin_script/test_correction_des_symboles/test_correction_des_symboles.sh" "$elf_file" ; then
            echo "        Test passed for $elf_file"
          else
            echo "        Test failed for $elf_file"
            rm  "$chemin"/*.o.modified
            break
          fi
        fi
      done
      rm  "$chemin"/*.o.modified
      ;;
    8)
      # Boucle 8 - Test de la correction des sections

      echo "--Exécution de la boucle 8 -elf_test_file Test de la correction des sections--"
      for elf_file in "$chemin"/example*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          rm -f etape8.txt
          touch etape8.txt
          ./Program2 "$elf_file" output.o >> erreur.txt
          if "$chemin_script/Reimplantations_de_type_R_ARM_ABS/test.sh" etape8.txt ; then
            echo "        Test passed for $elf_file"
          else
            echo "        Test failed for $elf_file"
            #rm test/Reimplantations_de_type_R_ARM_ABS/*.txt
            break
          fi
          rm test/Reimplantations_de_type_R_ARM_ABS/*.txt
        fi
      done;;
      9) 
      # Boucle 9 - Test de la Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL
      echo "--Exécution de la boucle 9 - Test de la Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL--"
      for elf_file in "$chemin"/*.o; do
        if [ -f "$elf_file" ]; then
          echo "    Exécution du test pour le fichier: $(basename $elf_file)"
          rm -f etape9.txt
          touch etape9.txt
          ./Program2 "$elf_file" output.o >> erreur.txt
          #si etape9.txt n'est pas vide
          if "$chemin_script/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/test.sh" etape9.txt ; then
            echo "        Test passed for $elf_file"
          else
            echo "        Test failed for $elf_file"
            exit 1
            rm -f ./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/*.txt
            break
            
          fi
          rm -f ./test/Reimplantations_de_type_R_ARM_JUMP24_et_R_ARM_CALL/*.txt
        fi
      done;;
    11)
      # Boucle 11 - Test de la Production d’un fichier ex´ecutable non relogeable
      echo "--Exécution de la boucle 11 - Test de la renumérotation des sections--"
      ./test/test_exec/auto.sh
      ;;
    *)
      echo "Numéro de boucle invalide: $seq. Veuillez spécifier un numéro entre 1 et 6."
      rm -f "$chemin"/elf_test_file/*.o
      exit 1
      ;;
  esac
done

# Nettoyage des fichiers objets après chaque exécution
echo "Nettoyage des fichiers objets..."
rm -f "$chemin"/elf_test_file/*.o
