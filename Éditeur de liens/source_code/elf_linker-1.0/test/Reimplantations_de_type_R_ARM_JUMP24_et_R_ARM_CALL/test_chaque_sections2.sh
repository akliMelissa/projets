
# Fonction de recherche de valeur dans le dump hexadécimal
recherche_valeur() {
   local partie="$1"        # Partie du texte où chercher
   local valeur_cible="$2"  # Valeur à chercher
   local position="$3"      # Position spécifique (-1 si on cherche partout)
   local message="$4"       # Message à afficher si trouvé
   local offset="$5"        # Offset pour afficher la position en hexadécimal
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

               # Multiply by 2
               offset_last_digit_dec=$((offset_last_digit_dec * 2 + 2))
               
               # Remove spaces from line
               line=$(echo "$line" | tr -d ' ')
               
               # Calculate extraction position
               extract_position=$((offset_last_digit_dec + 11))
               nb_caracteres=6
               # Extract 8 characters
               extracted_value=$(echo "$line" | cut -c${extract_position}-$((extract_position + nb_caracteres - 1)))
               
           fi
           ((word_count++))
       fi
   done < <(echo "$partie")
   echo "Extracted value: $extracted_value"

   # Normalisation des valeurs pour la comparaison
   # Convertir les valeurs en décimal
   extracted_dec=$(printf "%d" "0x$extracted_value" 2>/dev/null)
  # Recherche de l'addend à la position spécifique dans partie1
echo "=== Recherche de l'addend à la position spécifique ===" target_dec=$(printf "%d" "$valeur_cible" 2>/dev/null)
   
   # Reconvertir en hexadécimal avec le même format
   extracted_hex=$(printf "0x%x" "$extracted_dec")
   target_hex=$(printf "0x%x" "$target_dec")
   #valeure cible en hexadécimal

    if [ "$extracted_dec" = "$valeur_cible" ]; then
       echo "$message"
       found_match=true
       
    else 
         echo "Valeur incorrecte à la position spécifique"
         echo "Valeur cible: $valeur_cible"
        echo "Valeur extraite: $extracted_hex"
        return 1
   fi
   return 0

   
}

# Extraction des parties principales
partie1=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==1' "$1")
partie2=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==2' "$1")
partie3=$(awk 'BEGIN{RS="";ORS="\n\n"} NR==3' "$1")




offset=$(echo "$partie2" | grep "Offset_rel:" | grep -o "0x[0-9A-Fa-f]*")
valeur_symbol=$(echo "$partie2" | grep "Valeur de symbol" | grep -o "0x[0-9A-Fa-f]*")
addend=$(echo "$partie2" | grep "Addend" | grep -o "0x[0-9A-Fa-f]*")

# Conversion des valeurs hexadécimales en décimal
offset_dec=$(printf "%d" "$offset" 2>/dev/null)
#Position c'est le le chiffre decimal de offset 
ligne=$(echo "$offset" | cut -c1-$((${#offset}-1)))

#converit en decimal de ligne
ligne=$(printf "%d" "$ligne" 2>/dev/null)
# Recherche de l'addend à la position spécifique dans partie1, forcer sur 4 octets
diff=$((($valeur_symbol - $offset) & 0xFFFFFFFF))

# Convertir en hexadécimal (forcer sur 4 octets)
diff_hex=$(printf "0x%08x" "$diff")
echo "Diff hex: $diff_hex"

# Pour Thumb, limiter l'opération à 4 octets
masked_dif=$((($diff & 0x01FFFFFE) & 0xFFFFFFFF))

# Décalage, forcer sur 4 octets
shifted_diff1=$((($masked_dif >> 2) & 0xFFFFFFFF))
echo "Shifted Diff Thumb: $shifted_diff1"

# Pour ARM, limiter l'opération à 4 octets
masked_diff2=$((($diff & 0x03FFFFFE) & 0xFFFFFFFF))

# Décalage, forcer sur 4 octets
shifted_diff2=$((($masked_diff2 >> 2) & 0xFFFFFFFF))
echo "Shifted Diff ARM: $shifted_diff2"



recherche_valeur "$partie3" "$shifted_diff2" "$ligne" "Bonne valeur trouvé à la position spécifique" "$offset"
result1=$?
recherche_valeur "$partie3" "$shifted_diff1" "$ligne" "Bonne valeur trouvé à la position spécifique" "$offset" 
result2=$?

if [ $result1 -eq 0 ] || [ $result2 -eq 0 ]; then
    echo "Success "
else
    echo "Error - no matches found"
    exit 1
fi