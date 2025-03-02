

#!/bin/bash

# Vérification du paramètre
if [ $# -ne 1 ]; then
    echo "Usage: $0 <fichier_entrée>"
    exit 1
fi
addend_size=0
# Vérification de l'existence du fichier
if [ ! -f "$1" ]; then
    echo "Erreur: Le fichier $1 n'existe pas"
    exit 1
fi

tester_taille_bits() {
    local valeur=$1
    
    # Enlever le préfixe "0x" de la valeur hexadécimale pour compter uniquement les chiffres
    valeur_sans_prefixe="${valeur#0x}"

    # Compter le nombre de caractères dans la chaîne hexadécimale
    longueur=${#valeur_sans_prefixe}

    # Déterminer la taille en fonction du nombre de caractères
    if (( longueur <= 2 )); then
        addend_size=8
    elif (( longueur <= 4 )); then
        addend_size=16
    elif (( longueur <= 8 )); then
        addend_size=32
    fi
}

# Fonction de recherche de valeur dans le dump hexadécimal
recherche_valeur() {
   local partie="$1"        # Partie du texte où chercher
   local valeur_cible="$2"  # Valeur à chercher
   local position="$3"      # Position spécifique (-1 si on cherche partout)
   local message="$4"       # Message à afficher si trouvé
   local offset="$5"        # Offset pour afficher la position en hexadécimal
   local addend_size="$6"
          # Taille de l'addend en bits
   local word_count=0
   local found_match=false
   
   while read -r line; do
       if [[ "$line" =~ ^[[:space:]]*0x ]]; then
           values=$(echo "$line" | awk '{for(i=2;i<=5;i++) print $i}')
           
           if [ $position -eq -1 ] || [ $word_count -eq $position ]; then
               
               
               offset_hex=$(echo "$offset" | sed 's/^0x//')
                
               # Extract last hexadecimal digit
               offset_last_digit="${offset_hex: -1}"

               # Convert last hex digit to decimal
               offset_last_digit_dec=$(printf "%d" "0x$offset_last_digit" 2>/dev/null)
               x=$offset_last_digit_dec
               # Multiply by 2
               offset_last_digit_dec=$((offset_last_digit_dec * 2))
               
               # Remove spaces from line
               line=$(echo "$line" | tr -d ' ')
               
               
               # Calculate extraction position
               extract_position=$((offset_last_digit_dec + 11))
               if [[ $x -le 12 ]]; then
                    nb_caracteres=$(($addend_size/4))
                    extracted_value=$(echo "$line" | cut -c${extract_position}-$((extract_position + nb_caracteres - 1)))
                else
                    echo "OFFSET: $offset_last_digit_dec"
                    #diviser par 2 addend_size
                    taille=$((addend_size / 2))
                    echo "TAILLE: $taille"
                    nb_caracteres=$(($taille - x))
                   #multiply by 2   
                    nb_caracteres=$((nb_caracteres * 2))
                    extracted_value1=$(echo "$line" | cut -c${extract_position}-$((extract_position + nb_caracteres - 1)))
                    echo "NB_CARACTERES: $nb_caracteres"
                    #lire une nouvlle ligne
                    read -r line_new
                    newtaille=$((taille / 2))
                    nb_caracteres=$((newtaille - nb_caracteres)) 
                    echo "NB_CARACTERES: $nb_caracteres"
                    #formatter new line pas despaces
                    line_new=$(echo "$line_new" | tr -d ' ')
                    #extraire a partir de la position 10 newtaille caracteres
                    extracted_value=$(echo "$line_new" | cut -c11-$((10 + nb_caracteres )))
                    echo "Valeur extraite--1--------: $extracted_value"
                    echo "Valeur extraite--2--------: $extracted_value1"
                    #concatener les deux valeurs
                    extracted_value=$extracted_value1$extracted_value
                    echo "Valeur extraite--3--------: $extracted_value"
               fi  
               
               # Extract 8 characters
               
               
               
           fi
           ((word_count++))
       fi
   done < <(echo "$partie")
    echo "Valeur extraite: $extracted_value"
   # Normalisation des valeurs pour la comparaison
   # Convertir les valeurs en décimal
   extracted_dec=$(printf "%d" "0x$extracted_value" 2>/dev/null)
  # Recherche de l'addend à la position spécifique dans partie1
   echo "=== Recherche de l'addend à la position spécifique ===" target_dec=$(printf "%d" "$valeur_cible" 2>/dev/null)
   
   # Reconvertir en hexadécimal avec le même format
   extracted_hex=$(printf "0x%x" "$extracted_dec")
   echo "extracted_hex: $extracted_hex"
   target_hex=$(printf "0x%x" "$target_dec")
    


    # Normaliser les valeurs à 8 chiffres hexadécimaux
    formatted_cible=$(printf "%08x" $((valeur_cible)))
    formatted_extracted=$(printf "%08x" $((extracted_hex)))

    if [ "$formatted_extracted" = "$formatted_cible" ]; then
        echo "$message"
        found_match=true
    else
        echo "Non trouvé : La valeur à la position ne correspond pas à l'addend"
        echo "Valeur cible: $formatted_cible"
        echo "Valeur extraite: $formatted_extracted"
        exit 1
    fi

}



# Extraction des parties principales
partie1=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==1' "$1")
partie2=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==2' "$1")
partie3=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==3' "$1")




# Vérifier si la chaîne se termine par "n'a pas de données à afficher."
if [[ "$partie1" =~ n\'a\ pas\ de\ données\ à\ afficher\.$ ]]; then
    exit 0
fi
# Extraction des valeurs spécifiques de la partie 2
offset=$(echo "$partie2" | grep "Offset:" | grep -o "0x[0-9A-Fa-f]*")
valeur_symbol=$(echo "$partie2" | grep "Valeur de symbol" | grep -o "0x[0-9A-Fa-f]*")
addend=$(echo "$partie2" | grep "Addend" | grep -o "0x[0-9A-Fa-f]*")

tester_taille_bits "$addend"
echo "offset: $offset"
echo "valeur_symbol: $valeur_symbol"
echo "addend: $addend"
# Conversion des valeurs hexadécimales en décimal
offset_dec=$(printf "%d" "$offset" 2>/dev/null)

#Position c'est le le chiffre decimal de offset 
ligne=$(echo "$offset" | cut -c1-$((${#offset}-1)))
echo "addend_size: $addend_size"

#converit en decimal de ligne
ligne=$(printf "%d" "$ligne" 2>/dev/null)
# Recherche de l'addend à la position spécifique dans partie1
echo "=== Recherche de l'addend à la position spécifique ==="
recherche_valeur "$partie1" "$addend" "$ligne" "Trouvé : La valeur à la position correspond à l'addend" $offset $addend_size

# Calcul de la somme et recherche dans partie3
somme=$(printf "0x%x" $(($(printf "%d" "$valeur_symbol") + $(printf "%d" "$addend"))))
echo -e "\n=== Recherche de la somme dans la partie 3 ==="

recherche_valeur "$partie3" "$somme" "$ligne" "Trouvé : La somme des valeurs de symbol et de l'addend" $offset $addend_size
exit 0